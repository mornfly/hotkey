package com.jd.platform.hotkey.dashboard.util;

import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.model.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class CommonUtil {


    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);


    /**
     * 获取父级Key
     *
     * @param key key
     * @return string
     */
    public static String parentK(String key) {
        if (key.endsWith("/")) {
            key = key.substring(0, key.length() - 1);
        }
        int index = key.lastIndexOf("/");
        return key.substring(0, index + 1);
    }

    /**
     * 获取AppName
     *
     * @param k k
     * @return str
     */
    public static String appName(String k) {
        String[] arr = k.split("/");
        for (int i = 0; i < arr.length; i++) {
            if (i == 3) {
                return arr[i];
            }
        }
        return null;
    }




    public static String encoder(String text) {
        try {
            return Base64.getEncoder().encodeToString(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String decoder(String text) {
        byte[] bytes = Base64.getDecoder().decode(text);
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 分组
     *
     * @param list list
     * @return map
     */
    private static Map<String, List<Statistics>> listGroup(List<Statistics> list) {
        return list.stream().collect(Collectors.groupingBy(Statistics::getRule));
       // return list.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
    }

    /**
     * 分组
     *
     * @param list list
     * @return map
     */
    private static Map<Integer, List<Statistics>> listGroupByTime(List<Statistics> list, boolean isMinute) {
        if (isMinute) {
            return list.stream().collect(Collectors.groupingBy(Statistics::getMinutes));
        }
        return list.stream().collect(Collectors.groupingBy(Statistics::getHours));
    }


    /**
     * 处理数据
     *
     * @param st       开始时间
     * @param et       结束时间
     * @param list     数据
     * @param isMinute 类型
     * @return vo
     */
    public static HotKeyLineChartVo processData(LocalDateTime st, LocalDateTime et, List<Statistics> list,
                                                boolean isMinute, List<String> rules, String app) {
        Set<String> xAxisSet = new TreeSet<>();
        Duration duration = Duration.between(st, et);
        long passTime = isMinute ? duration.toMinutes() : duration.toHours();
        Map<Integer, Integer> timeCountMap = new TreeMap<>();
        String pattern = isMinute ? DateUtil.PATTERN_MINUS : DateUtil.PATTERN_HOUR;
        for (int i = 1; i < passTime; i++) {
            int time = DateUtil.reviseTime(st, i, isMinute ? 1 : 2);
            xAxisSet.add(DateUtil.formatTime(time, pattern));
            timeCountMap.put(time, null);
        }
        Map<String, List<Statistics>> ruleStatsMap = listGroup(list);
        Map<String, List<Integer>> ruleDataMap = new ConcurrentHashMap<>(ruleStatsMap.size());
        ruleStatsMap.forEach((rule, statistics) -> {
            Map<Integer, List<Statistics>> timeStatsMap = listGroupByTime(statistics, isMinute);
            timeCountMap.forEach((k, v) -> {
                if (timeStatsMap.get(k) == null) {
                    timeCountMap.put(k, 0);
                } else {
                    timeCountMap.put(k, timeStatsMap.get(k).get(0).getCount());
                }
            });
            ruleDataMap.put(rule, new ArrayList<>(timeCountMap.values()));
        });
        HotKeyLineChartVo vo = new HotKeyLineChartVo();
        vo.setxAxis2(xAxisSet);
        if (!StringUtils.isEmpty(app)) {
            ruleDataMap.forEach((rule, data) -> {
                if (!rule.startsWith(app)) {
                    ruleDataMap.remove(rule);
                }
            });
        }
        vo.setSeries2(ruleDataMap);
        Set<String> ruleSet = ruleDataMap.keySet();
        Set<String> etcdRuleSet = new HashSet<>(rules);
        Set<String> legend = new CopyOnWriteArraySet<>(etcdRuleSet);
        legend.retainAll(ruleSet);
        if (!StringUtils.isEmpty(app)) {
            legend.forEach(x -> {
                if (!x.startsWith(app)) {
                    legend.remove(x);
                }
            });
        }
        vo.setLegend(legend);
        return vo;
    }


    /**
     * build存入对象
     *
     * @param key key是 appName + #**# + pin__#**#2020-10-23 21:11:22
     * @param map value是"67-1937" 前面是热key访问量，后面是总访问量
     * @return Summary
     */
    public static Summary buildSummary(String key, Map<String, String> map) {
        String[] args = key.split(com.jd.platform.hotkey.common.tool.Constant.BAK_DELIMITER);
        String app = args[0];
        String rule = args[1];
        String hitTime = args[2];
        Date time = DateUtil.strToDate(hitTime);
        assert time != null;
        LocalDateTime ldt = DateUtil.dateToLdt(time);
        int day = DateUtil.nowDay(ldt);
        int hour = DateUtil.nowHour(ldt);
        int minus = DateUtil.nowMinus(ldt);
        long seconds = time.getTime() / 1000;
        String[] counts = map.get(key).split("-");
        int hitCount = Integer.parseInt(counts[0]);
        int totalCount = Integer.parseInt(counts[1]);
        String uuid = app + "-" + rule + hitTime;

        return Summary.aSummary().indexName(rule).rule(rule).app(app)
                .val1(totalCount).val2(hitCount).val3(BigDecimal.ZERO)
                .days(day).hours(hour).minutes(minus).seconds((int) seconds)
                .bizType(1).uuid(uuid).createTime(new Date()).build();
    }

}
