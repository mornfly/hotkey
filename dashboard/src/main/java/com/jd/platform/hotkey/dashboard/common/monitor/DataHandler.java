package com.jd.platform.hotkey.dashboard.common.monitor;


import com.alibaba.fastjson.JSON;
import com.ibm.etcd.api.Event;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.StatisticsMapper;
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
            TwoTuple<KeyTimely, KeyRecord> twoTuple;
            try {
                twoTuple = handHotKey(queue.take());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("handHotKey error ," + e.getCause());
                continue;
            }
            KeyRecord keyRecord = twoTuple.getSecond();
            KeyTimely keyTimely = twoTuple.getFirst();

            if (keyTimely.getUuid() == null) {
                keyTimelyMapper.deleteByKeyAndApp(keyTimely.getKey(), keyTimely.getAppName());
            } else {
                try {
                    keyTimelyMapper.insertSelective(keyTimely);
                } catch (Exception e) {
                    log.info("insert timely error");
                }
            }

            if (keyRecord != null) {
                //插入记录
                keyRecordMapper.insertSelective(keyRecord);
            }

        }

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
        String uuid = eventWrapper.getUuid();
        int type = eventType.getNumber();

        //组建成对象，供累计后批量插入、删除
        TwoTuple<KeyTimely, KeyRecord> timelyKeyRecordTwoTuple = new TwoTuple<>();
        if (eventType.equals(Event.EventType.PUT)) {
            //手工添加的是时间戳13位，worker传过来的是uuid
            String source = value.length() == 13 ? Constant.HAND : Constant.SYSTEM;
            timelyKeyRecordTwoTuple.setFirst(new KeyTimely(key, value, appName, ttl, uuid, date));

            // 这是一个骚操作 在线上不方便新增rule字段的时候 临时用下val
            String rule = RuleUtil.rule(appKey);
            KeyRecord keyRecord = new KeyRecord(key, rule, appName, ttl, source, type, uuid, date);
            keyRecord.setRule(rule);
            timelyKeyRecordTwoTuple.setSecond(keyRecord);
            return timelyKeyRecordTwoTuple;
        } else if (eventType.equals(Event.EventType.DELETE)) {
            timelyKeyRecordTwoTuple.setFirst(new KeyTimely(key, null, appName, 0L, null, null));
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

}
