package com.jd.platform.hotkey.dashboard.util;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
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


	public static String keyName(String k) {
		int index = k.lastIndexOf("/");
		return k.substring(index + 1);
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
	 * 拼装数据
	 * @param list list-data
	 * @param startTime 开始时间
	 * @param size 格子数
	 * @param type 类型 1分钟 2小时
	 * @return vo
	 */
	public static HotKeyLineChartVo assembleData(List<Statistics> list, LocalDateTime startTime, int size, int type) {
		Set<String> set = new TreeSet<>();
		boolean isHour = type == 1;
		String suffix = isHour ? "60" : "24";
		String pattern = isHour ? DateUtil.PATTERN_MINUS : DateUtil.PATTERN_HOUR;
		Map<String, int[]> map = new HashMap<>(10);
		Map<String, List<Statistics>> listMap = listGroup(list);
//		log.info("按照rule分组以后的listMap--> {}", JSON.toJSONString(listMap));
		for (Map.Entry<String, List<Statistics>> m : listMap.entrySet()) {
			int start = DateUtil.reviseTime(startTime, 0, type);
			map.put(m.getKey(), new int[size]);
			int[] data = map.get(m.getKey());
			int tmp = 0;
			for (int i = 0; i < size; i++) {
				if (String.valueOf(start).endsWith(suffix)) {
					LocalDateTime tmpTime = DateUtil.strToLdt((start - 1) + "", pattern);
					start = DateUtil.reviseTime(tmpTime, 1, type);
				}
//				log.info("start--> {},  tmp---> {} ", start, tmp);
				set.add(DateUtil.strToLdt(start + "", pattern).toString().replace("T", " "));
				Statistics st = m.getValue().get(tmp);
				int val = isHour ? st.getMinutes() : st.getHours();
				if (start != val) {
					data[i] = 0;
				} else {
					tmp++;
					data[i] = st.getCount();
				}
				start++;
			}
		}
		return new HotKeyLineChartVo(new ArrayList<>(set), map);
	}


	/**
	 * 分组
	 * @param list list
	 * @return map
	 */
	private static Map<String, List<Statistics>> listGroup(List<Statistics> list){
		if(Constant.VERSION != 1){
		   return list.stream().collect(Collectors.groupingBy(Statistics::getRule));
		}
		return  list.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
	}

	/**
	 * 分组
	 * @param list list
	 * @return map
	 */
	private static Map<Integer, List<Statistics>> listGroupByTime(List<Statistics> list, boolean isMinute){
		if(isMinute){
			return  list.stream().collect(Collectors.groupingBy(Statistics::getMinutes));
		}
		return list.stream().collect(Collectors.groupingBy(Statistics::getHours));
	}


	/**
	 * 处理数据
	 * @param st 开始时间
	 * @param et 结束时间
	 * @param list 数据
	 * @param isMinute 类型
	 * @return vo
	 */
	public static HotKeyLineChartVo processData(LocalDateTime st, LocalDateTime et, List<Statistics> list,
												boolean isMinute, List<String> rules, String app){
		Set<String> xAxisSet = new TreeSet<>();
		Duration duration = Duration.between(st,et);
		long passTime = isMinute ? duration.toMinutes() : duration.toHours();
		Map<Integer, Integer> timeCountMap =  new TreeMap<>();
		String pattern = isMinute ? DateUtil.PATTERN_MINUS : DateUtil.PATTERN_HOUR;
		for (int i = 1; i < passTime; i++) {
			int time = DateUtil.reviseTime(st, i, isMinute ? 1:2);
			xAxisSet.add(DateUtil.formatTime(time, pattern));
			timeCountMap.put(time,null);
		}
		Map<String, List<Statistics>> ruleStatsMap = listGroup(list);
		Map<String, List<Integer>> ruleDataMap = new ConcurrentHashMap<>(ruleStatsMap.size());
		ruleStatsMap.forEach((rule,statistics)->{
			String app1 = statistics.get(0).getApp();
			rule = app1 + "-" + rule;
			Map<Integer, List<Statistics>> timeStatsMap = listGroupByTime(statistics, isMinute);
			timeCountMap.forEach((k,v)->{
				if(timeStatsMap.get(k) == null){
					timeCountMap.put(k,0);
				}else{
					timeCountMap.put(k,timeStatsMap.get(k).get(0).getCount());
				}
			});
			ruleDataMap.put(rule, new ArrayList<>(timeCountMap.values()));
		});
		HotKeyLineChartVo vo = new HotKeyLineChartVo();
		vo.setxAxis2(xAxisSet);
		if(StringUtils.isNotEmpty(app)){
			ruleDataMap.forEach((rule,data)->{
				if(!rule.startsWith(app)){
					ruleDataMap.remove(rule);
				}
			});
		}
		vo.setSeries2(ruleDataMap);
		Set<String> ruleSet = ruleDataMap.keySet();
		Set<String> etcdRuleSet = new HashSet<>(rules);
		Set<String> legend = new CopyOnWriteArraySet<>(etcdRuleSet);
		legend.retainAll(ruleSet);
		if(StringUtils.isNotEmpty(app)){
			legend.forEach(x->{
				if(!x.startsWith(app)){ legend.remove(x); }
			});
		}
		vo.setLegend(legend);
		return vo;
	}


}
