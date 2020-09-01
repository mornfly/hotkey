package com.jd.platform.hotkey.dashboard.common.monitor;


import com.alibaba.fastjson.JSON;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.rule.KeyRule;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.StatisticsMapper;
import com.jd.platform.hotkey.dashboard.mapper.SummaryMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import com.jd.platform.hotkey.dashboard.util.RuleUtil;
import com.jd.platform.hotkey.dashboard.util.TwoTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DataHandler {

    private static Logger log = LoggerFactory.getLogger(DataHandler.class);

    @Resource
    private KeyRecordMapper keyRecordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;
    @Resource
    private StatisticsMapper statisticsMapper;

    @Resource
    private SummaryMapper summaryMapper;

    @Resource
    private IConfigCenter configCenter;


    /**
     * 队列
     */
    private BlockingQueue<EventWrapper> queue = new LinkedBlockingQueue<>();

    /**
     * 入队
     */
    public void offer(EventWrapper eventWrapper) {
        try {
            queue.put(eventWrapper);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertRecords() {
        while (true) {
            EventWrapper eventWrapper;
            try {
                eventWrapper = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            try {
                TwoTuple<KeyTimely, KeyRecord> twoTuple = handHotKey(eventWrapper);
                if (twoTuple == null) {
                    continue;
                }
                KeyRecord keyRecord = twoTuple.getSecond();
                KeyTimely keyTimely = twoTuple.getFirst();

                if (keyTimely.getUuid() == null) {
                    keyTimelyMapper.deleteByKeyAndApp(keyTimely.getKey(), keyTimely.getAppName());
                } else {
                    try {
                        keyTimelyMapper.saveOrUpdate(keyTimely);
                    } catch (Exception e) {
                        log.info("insert timely error",e);
                    }
                }

                if (keyRecord != null) {
                    //插入记录
                    keyRecordMapper.insertSelective(keyRecord);
                }
            } catch (Exception e) {
                log.error("eventWrapper:" + eventWrapper);
                e.printStackTrace();
                log.error("handHotKey error ," + e.getCause());
            }


        }

    }

    /**
     * 插入记录表
     * @param type 0是新增，1是删除
     */
    public void insertRecord(String appKey, int type) {
        String source = Constant.SYSTEM;
        String rule = RuleUtil.rule(appKey);
        KeyRule keyRule = RuleUtil.findByKey(appKey);
        //appName+"/"+"key"
        String[] arr = appKey.split("/");
        String appName = arr[0];
        String key = arr[1];
        String uuid = UUID.randomUUID().toString();
        KeyRecord keyRecord = new KeyRecord(key, rule, appName, (long)keyRule.getDuration(), source, type, uuid, new Date());
        keyRecord.setRule(rule);
        keyRecordMapper.insertSelective(keyRecord);
    }


    /**
     * 处理热点key和记录
     */
    private TwoTuple<KeyTimely, KeyRecord> handHotKey(EventWrapper eventWrapper) {
        Date date = eventWrapper.getDate();
        long ttl = eventWrapper.getTtl();
        Event.EventType eventType = eventWrapper.getEventType();
        String appKey = eventWrapper.getKey();
        String value = eventWrapper.getValue();
        //appName+"/"+"key"
        String[] arr = appKey.split("/");
        String appName = arr[0];
        String key = arr[1];
        String uuid = UUID.randomUUID().toString();
        int type = eventType.getNumber();

        //组建成对象，供累计后批量插入、删除
        TwoTuple<KeyTimely, KeyRecord> timelyKeyRecordTwoTuple = new TwoTuple<>();
        if (eventType.equals(Event.EventType.PUT)) {
            //如果是客户端删除时发出的put指令
            if (com.jd.platform.hotkey.common.tool.Constant.DEFAULT_DELETE_VALUE.equals(value)) {
                log.info("client remove key event : " + appKey);
                return null;
            }
            //手工添加的是时间戳13位，worker传过来的是uuid
            String source = value.length() == 13 ? Constant.HAND : Constant.SYSTEM;
            timelyKeyRecordTwoTuple.setFirst(KeyTimely.aKeyTimely().key(key).val(value).appName(appName).duration(ttl).uuid(appKey).createTime(date).build());
            String rule = RuleUtil.rule(appKey);
            KeyRecord keyRecord = new KeyRecord(key, rule, appName, ttl, source, type, uuid, date);
            keyRecord.setRule(rule);
            timelyKeyRecordTwoTuple.setSecond(keyRecord);
            return timelyKeyRecordTwoTuple;
        } else if (eventType.equals(Event.EventType.DELETE)) {
            timelyKeyRecordTwoTuple.setFirst(KeyTimely.aKeyTimely().key(key).appName(appName).build());
            return timelyKeyRecordTwoTuple;
        }
        return timelyKeyRecordTwoTuple;
    }


    /**
     * 每小时 统计一次record 表 结果记录到统计表
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void offlineStatistics() {
        try {
            LocalDateTime now = LocalDateTime.now();
            Date nowTime = DateUtil.ldtToDate(now);
            int day = DateUtil.nowDay(now);
            int hour = DateUtil.nowHour(now);
            SearchReq preHour = new SearchReq(now.minusHours(1));
            List<Statistics> records = keyRecordMapper.maxHotKey(preHour);
            if (records.size() != 0) {
                records.forEach(x -> {
                    x.setBizType(1);
                    x.setCreateTime(nowTime);
                    x.setDays(day);
                    x.setHours(hour);
                    x.setUuid(1 + "_" + x.getKeyName() + "_" + hour);
                });
            }
            log.info("每小时统计最热点,时间：{}, 行数：{}", now.toString(), records.size());
            List<Statistics> statistics = keyRecordMapper.statisticsByRule(preHour);
            if (statistics.size() != 0) {
                statistics.forEach(x -> {
                    x.setBizType(6);
                    x.setRule(x.getRule());
                    x.setCreateTime(nowTime);
                    x.setDays(day);
                    x.setHours(hour);
                    x.setUuid(6 + "_" + x.getKeyName() + "_" + hour);
                });
                log.info("每小时统计规则,时间：{}, data list：{}", now.toString(), JSON.toJSONString(statistics));
                records.addAll(statistics);
            }
            if (records.size() > 0) {
                int row = statisticsMapper.batchInsert(records);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 每分钟统计一次record 表 结果记录到统计表
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void offlineStatisticsRule() {
        try {
            LocalDateTime now = LocalDateTime.now();
            Date nowTime = DateUtil.ldtToDate(now);
            int day = DateUtil.nowDay(now);
            int hour = DateUtil.nowHour(now);
            int minus = DateUtil.nowMinus(now);

            List<Statistics> records = keyRecordMapper.statisticsByRule(new SearchReq(now.minusMinutes(1)));
            if (records.size() == 0) {
                return;
            }
            records.forEach(x -> {
                x.setBizType(5);
                x.setRule(x.getRule());
                x.setCreateTime(nowTime);
                x.setDays(day);
                x.setHours(hour);
                x.setMinutes(minus);
                // 骚操作 临时解决没有rule字段的问题
                x.setUuid(5 + "_" + x.getKeyName() + "_" + minus);
            });
            int row = statisticsMapper.batchInsert(records);
//            log.info("每分钟统计规则，时间：{}, 影响行数：{}, data list:{}", now.toString(), row, JSON.toJSONString(records));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 每天根据app的配置清理过期数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void clearExpireData() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.clearCfgPath);
            for (KeyValue kv : keyValues) {
                String key = kv.getKey().toStringUtf8();
                String ttl = kv.getValue().toStringUtf8();
                String app = key.replace(ConfigConstant.clearCfgPath,"");
                Date expireDate = DateUtil.ldtToDate(now.minusDays(Integer.parseInt(ttl)));
                summaryMapper.clearExpireData(app, expireDate);
                keyRecordMapper.clearExpireData(app, expireDate);
                statisticsMapper.clearExpireData(app, expireDate);
            }
          } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
