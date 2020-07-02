package com.jd.platform.hotkey.dashboard.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ibm.etcd.api.Event;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.ReceiveCountMapper;
import com.jd.platform.hotkey.dashboard.mapper.StatisticsMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.ReceiveCount;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import com.jd.platform.hotkey.dashboard.util.RuleUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ProjectName: hotkey
 * @ClassName: KeyServiceImpl
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 17:53
 */
@Service
public class KeyServiceImpl implements KeyService {

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private KeyRecordMapper recordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;
    @Resource
    private ReceiveCountMapper countMapper;
    @Resource
    private StatisticsMapper statisticsMapper;
    @Resource
    private RuleService ruleService;

    /**
     * 折线图
     * @param req req
     * @return vo
     */
    @Override
    public HotKeyLineChartVo ruleLineChart(SearchReq req, String app) {
        int type = req.getType();
        String appReq = req.getApp();
        // admin 全查
        if(StringUtils.isNotEmpty(appReq)){
            app = appReq;
        }
        req.setApp(null);
        LocalDateTime now = LocalDateTime.now();
        req.setEndTime(req.getEndTime() == null ? DateUtil.ldtToDate(now) : req.getEndTime());
        List<String> rules = ruleService.listRules(null);
        if(type == 4){
            LocalDateTime st = req.getStartTime() == null? now.minusMinutes(31) : DateUtil.dateToLdt(req.getStartTime());
            req.setStartTime(DateUtil.ldtToDate(st));
            LocalDateTime et = DateUtil.dateToLdt(req.getEndTime());
            boolean longTime = Duration.between(st,et).toHours()>2;
            req.setType(longTime ? 6 : 5);
            List<Statistics> list = statisticsMapper.listOrderByTime(req);
            return CommonUtil.processData(st,et,list,!longTime,rules,app);
        }

        if(type == 5){
            LocalDateTime startTime = now.minusMinutes(31);
            req.setStartTime(DateUtil.ldtToDate(startTime));
            List<Statistics> list = statisticsMapper.listOrderByTime(req);
            return CommonUtil.processData(startTime,now,list,true,rules,app);
        }else if(type == 6){
            LocalDateTime startTime2 = now.minusHours(25);
            req.setStartTime(DateUtil.ldtToDate(startTime2));
            List<Statistics> list2 = statisticsMapper.listOrderByTime(req);
            return CommonUtil.processData(startTime2,now,list2,false,rules,app);
        }else{
            LocalDateTime startTime3 = now.minusDays(7).minusHours(1);
            req.setStartTime(DateUtil.ldtToDate(startTime3));
            req.setType(6);
            List<Statistics> list3 = statisticsMapper.listOrderByTime(req);
            return CommonUtil.processData(startTime3,now,list3,false,rules,app);
        }
    }


    @Override
    public PageInfo<KeyTimely> pageKeyTimely(PageReq page, SearchReq param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyTimely> listKey = keyTimelyMapper.listKeyTimely(param);
        for (KeyTimely timely : listKey) {
            timely.setKey(CommonUtil.keyName(timely.getKey()));
            timely.setRuleDesc(RuleUtil.ruleDesc(timely.getAppName() + "/" + timely.getKey()));
        }
        return new PageInfo<>(listKey);
    }

    @Override
    public PageInfo<Statistics> pageMaxHot(PageReq page, SearchReq req) {
        checkParam(req);
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Statistics> statistics = statisticsMapper.sumStatistics(req);
        return new PageInfo<>(statistics);
    }

    @Override
    public List<Statistics> listMaxHot(SearchReq req) {
        checkParam(req);
        return statisticsMapper.sumStatistics(req);
    }


    @Override
    public HotKeyLineChartVo getLineChart(ChartReq chartReq) {
        int hours = 6;
        // 默认查询6小时内的数据
        SearchReq req = new SearchReq();
        req.setStartTime(DateUtil.preTime(hours));
        req.setEndTime(new Date());
        List<Statistics> statistics = statisticsMapper.listStatistics(req);
        // 获取data Y轴
        Map<String, int[]> keyDateMap = keyDateMap(statistics, hours);
        // 获取时间x轴
        List<String> list = new ArrayList<>();
        for (int i = hours; i >0 ; i--) {
            LocalDateTime time = LocalDateTime.now().minusHours(i-1);
            int hour = time.getHour();
            list.add(hour+"时");
        }
        return new HotKeyLineChartVo(list,keyDateMap);
    }


    @Override
    public HotKeyLineChartVo getQpsLineChart(ChartReq chartReq) {
        if(chartReq.getStartTime() == null || chartReq.getEndTime() == null){
         /*   chartReq.setStartTime(DateUtil.preTime());
            chartReq.setEndTime(new Date());*/
        }
        List<ReceiveCount> countList = countMapper.list(chartReq);
        Map<String, int []> map = new HashMap<>(10);
        Set<String> minutes = new HashSet<>();
        List<List<ReceiveCount>> workerList = new ArrayList<>();
        countList.stream().collect(Collectors.groupingBy(ReceiveCount::getWorkerName,Collectors.toList()))
                .forEach((name,data)-> workerList.add(data));
        for (List<ReceiveCount> cts : workerList) {
            int size = cts.size();
            for (int i = 0; i < size; i++) {
                ReceiveCount dto = cts.get(i);
                String k = dto.getWorkerName();
                Long v = dto.getReceiveCount();
                Integer ms = dto.getMinutes();
                minutes.add(ms.toString());
                if(map.get(k) == null){
                    int [] data = new int[size];
                    data[i] = v.intValue();
                    map.put(k, data);
                }else{
                    int [] data = map.get(k);
                    data[i] = v.intValue();
                    map.put(k, data);
                }
            }
        }
        return new HotKeyLineChartVo(new ArrayList<>(minutes),map);
    }


    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageReq page, SearchReq param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKeyRecord(param);
        for (KeyRecord keyRecord : listKey) {
            keyRecord.setRuleDesc(RuleUtil.ruleDesc(keyRecord.getAppName() + "/" + keyRecord.getKey()));
        }

        return new PageInfo<>(listKey);
    }


    @Override
    public int insertKeyByUser(KeyTimely key) {
        configCenter.putAndGrant(ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey(),
                System.currentTimeMillis() + "", key.getDuration());
        return 1;
    }

    @Override
    public int updateKeyByUser(KeyTimely key) {
        String ectdKey = ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey();
        configCenter.putAndGrant(ectdKey, "UPDATE", key.getDuration());
        return 1;
    }

    @Override
    public int delKeyByUser(KeyTimely keyTimely) {
        //app + "_" + key
        String[] arr = keyTimely.getKey().split("/");
        //删除client监听目录的key
        String ectdKey = ConfigConstant.hotKeyPath + arr[0] + "/" + arr[1];
        configCenter.delete(ectdKey);
        //也删除Record目录下的该key，因为不确定要删的key到底在哪
        String recordKey = ConfigConstant.hotKeyRecordPath + arr[0] + "/" + arr[1];
        configCenter.delete(recordKey);

        KeyRecord keyRecord = new KeyRecord(arr[1], "", arr[0], 0L, Constant.HAND,
                Event.EventType.DELETE_VALUE, UUID.randomUUID().toString(), new Date());

        recordMapper.insertSelective(keyRecord);

        return 1;
    }

    @Override
    public KeyTimely selectByKey(String key) {
        return keyTimelyMapper.selectByKey(key);
    }

    @Override
    public KeyTimely selectByPk(Long id) {
        return keyTimelyMapper.selectByPrimaryKey(id);
    }




    private Map<String, int[]> keyDateMap(List<Statistics> statistics, int hours){
        Map<String, int[]> map = new HashMap<>(10);
        Map<String, List<Statistics>> listMap = statistics.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
        for (Map.Entry<String, List<Statistics>> m : listMap.entrySet()) {
            int start = DateUtil.preHoursInt(5);
            map.put(m.getKey(),new int[hours]);
            int[] data = map.get(m.getKey());
            int tmp = 0;
            for (int i = 0; i < hours; i++) {
                Statistics st;
                try {
                    st = m.getValue().get(tmp);
                    if(String.valueOf(start).endsWith("24")){ start = start + 77; }
                    if(start != st.getHours()){
                        data[i] = 0;
                    }else{
                        tmp ++;
                        data[i] = st.getCount();
                    }
                    start++;
                }catch (Exception e){
                    data[i] = 0;
                }
            }
        }
        return map;
    }



    private void checkParam(SearchReq req) {
        if(req.getStartTime() == null || req.getEndTime() == null){
            req.setStartTime(DateUtil.preTime(5));
            req.setEndTime(new Date());
        }
       /* long day = (req.getEndTime().getTime() - req.getStartTime().getTime()) / 86400000;
        if( day > Constant.MAX_DAY_RANGE){
            throw new BizException(ResultEnum.TIME_RANGE_LARGE);
        }*/
    }


    private static  List<Statistics> statisticsList(){
        Random rd = new Random();
        List<Statistics> records = new ArrayList<>();
     //   statisticsMapper.clear(1);
        for (int i = 0; i < 35; i++) {
            if(i== 2||i== 3||i== 4||i== 25||i== 12||i== 13||i== 14||i== 30||i==32||i==33||i== 34){
                continue;
            }
            // 每分钟小时 统计一次record 表 结果记录到统计表
            LocalDateTime now = LocalDateTime.now().minusMinutes(35-i);
            Date nowTime = DateUtil.ldtToDate(now);
            int day = DateUtil.nowDay(now);
            int hour = DateUtil.nowHour(now);
            int minus = DateUtil.nowMinus(now);

            Statistics s1 = new Statistics();
            s1.setKeyName("k21");
            s1.setApp("key21APP");
            s1.setCreateTime(nowTime);
            s1.setDays(day);
            s1.setHours(hour);
            s1.setCount(rd.nextInt(800));
            s1.setBizType(1);
            s1.setMinutes(minus);
            s1.setUuid(1 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
            records.add(s1);

            Statistics s11 = new Statistics();
            s11.setKeyName("k22");
            s11.setApp("key22APP");
            s11.setBizType(1);
            s11.setCreateTime(nowTime);
            s11.setDays(day);
            s11.setMinutes(minus);
            s11.setCount(rd.nextInt(600));
            s11.setHours(hour);
            s11.setUuid(2 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
            records.add(s11);
        }

        if(1==1){
            return records;
        }
      //  statisticsMapper.batchInsert(records);
        List<Statistics> list = new ArrayList<>();
        for (int i = 0; i < 30 ; i++) {
            if(i == 12 || i ==13){
            }else{
                Statistics st = new Statistics();
                st.setApp("k21");
                st.setKeyName("k21");
                st.setCount(rd.nextInt(100));
                st.setBizType(1);
                //   LocalDateTime ldf = DateUtil.strToLdt("2006082355");
                //    System.out.println(ldf.toString()+"  -   "+DateUtil.strToLdt("2006082355"));
                int time = DateUtil.reviseTime(DateUtil.strToLdt("2006082355",DateUtil.PATTERN_MINUS), i, 1);
                //    System.out.println(time);
                st.setMinutes(time);
               // list.add(st);
            }
        }
        List<Statistics> list2 = new ArrayList<>();
        for (int i = 0; i < 30 ; i++) {
            if(i == 0 || i == 1  || i == 18 || i == 19 || i == 25){

            }else{
                Statistics st2 = new Statistics();
                st2.setApp("k22");
                st2.setKeyName("k22");
                st2.setCount(rd.nextInt(100));
                st2.setBizType(1);
                st2.setMinutes(DateUtil.reviseTime(DateUtil.strToLdt("2006082355", DateUtil.PATTERN_MINUS),i,1));
                list2.add(st2);
            }
        }
      //  list.addAll(list2);
        return list;

    }

    private List<Statistics> statisticsList1(int type){
        Random rd = new Random();
        List<Statistics> records = new ArrayList<>();
        statisticsMapper.clear(6);

        if(type == 3){
            int temp = 1;
            for (int j = 0; j < 9; j++) {
                for (int i = 0; i < 24; i++) {
                    // 每分钟小时 统计一次record 表 结果记录到统计表
                    LocalDateTime now = LocalDateTime.now().minusHours(9 * 24 - temp);

                    Date nowTime = DateUtil.ldtToDate(now);
                    int day = DateUtil.nowDay(now);
                    int hour = DateUtil.nowHour(now);
                    Statistics s1 = new Statistics();
                    s1.setKeyName("k21");
                    s1.setApp("key21APP");
                    s1.setCreateTime(nowTime);
                    s1.setDays(day);
                    s1.setHours(hour);
                    s1.setCount(rd.nextInt(200));
                    s1.setBizType(6);
                    s1.setUuid(1 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
                    records.add(s1);

                    Statistics s11 = new Statistics();
                    s11.setKeyName("k22");
                    s11.setApp("key22APP");
                    s11.setBizType(6);
                    s11.setCreateTime(nowTime);
                    s11.setDays(day);
                    s11.setCount(rd.nextInt(500));
                    s11.setHours(hour);
                    s11.setUuid(2 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
                    records.add(s11);

                    Statistics s33 = new Statistics();
                    s33.setKeyName("k33");
                    s33.setApp("key33APP");
                    s33.setBizType(6);
                    s33.setCreateTime(nowTime);
                    s33.setDays(day);
                    s33.setCount(rd.nextInt(300));
                    s33.setHours(hour);
                    s33.setUuid(3 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
                    records.add(s33);
                    temp++;
                }
            }


        }else{
            for (int i = 0; i < 30; i++) {
                // 每分钟小时 统计一次record 表 结果记录到统计表
                LocalDateTime now = LocalDateTime.now().minusHours(30-i);
                Date nowTime = DateUtil.ldtToDate(now);
                int day = DateUtil.nowDay(now);
                int hour = DateUtil.nowHour(now);
                Statistics s1 = new Statistics();
                s1.setKeyName("key21");
                s1.setApp("key21APP");
                s1.setCreateTime(nowTime);
                s1.setDays(day);
                s1.setHours(hour);
                s1.setCount(rd.nextInt(800));
                s1.setBizType(2);
                s1.setUuid(1 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
                records.add(s1);

                Statistics s11 = new Statistics();
                s11.setKeyName("key22");
                s11.setApp("key22APP");
                s11.setBizType(2);
                s11.setCreateTime(nowTime);
                s11.setDays(day);
                s11.setCount(rd.nextInt(600));
                s11.setHours(hour);
                s11.setUuid(2 + "_" + s1.getKeyName() + "_" + hour+ UUID.randomUUID().toString());
                records.add(s11);
            }
        }
        statisticsMapper.batchInsert(records);
        return records;
    }

}


