package com.jd.platform.hotkey.dashboard;


import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.common.tool.IpUtils;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.RulesMapper;
import com.jd.platform.hotkey.dashboard.mapper.SummaryMapper;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@EnableAsync
@EnableScheduling
@SpringBootApplication
public class DashboardApplication implements CommandLineRunner {


    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private SummaryMapper summaryMapper;
    @Resource
    private KeyTimelyMapper timelyMapper;

    public static void main(String[] args) {
        try {
            SpringApplication.run(DashboardApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void run(String... args) {
     /*   Map<String,String> map1 = new HashMap<>();
        String k1 = "cartsoa#**#rule1__#**#2020-10-23 21:11:21";
        String v1 = "50-200";
        String k2 = "cartsoa#**#rule2__#**#2020-10-23 21:11:22";
        String v2 = "30-100";
        map1.put(k1,v1);
        map1.put(k2,v2);
        for (String key : map1.keySet()) {
            int row = summaryMapper.saveOrUpdate(CommonUtil.buildSummary(key, map1));
        }

        Map<String,String> map12 = new HashMap<>();
        String k12 = "cartsoa#**#rule1__#**#2020-10-23 21:11:21";
        String v12 = "10-100";
        String k22 = "cartsoa#**#rule2__#**#2020-10-23 21:11:23";
        String v22 = "70-300";
        String kk22 = "cartsoa#**#rule2__#**#2020-10-23 21:11:22";
        String vv22 = "87-500";
        String k33 = "cartsoa#**#rule3__#**#2020-10-23 21:11:22";
        String v33 = "72-300";
        map12.put(k12,v12);
        map12.put(k22,v22);
        map12.put(k33,v33);
        map12.put(kk22,vv22);
        for (String key : map12.keySet()) {
            int row = summaryMapper.saveOrUpdate(CommonUtil.buildSummary(key, map12));
        }

        Map<String,String> map122 = new HashMap<>();
        String k122 = "cartsoa#**#rule1__#**#2020-10-23 21:11:23";
        String v122 = "44-55";
        String k222 = "cartsoa#**#rule2__#**#2020-10-23 21:11:25";
        String v222 = "66-77";
        String k332 = "cartsoa#**#rule3__#**#2020-10-23 21:11:27";
        String v332 = "88-99";
        map122.put(k122,v122);
        map122.put(k222,v222);
        map122.put(k332,v332);
        for (String key : map122.keySet()) {
            int row = summaryMapper.saveOrUpdate(CommonUtil.buildSummary(key, map122));
        }*/
        int row = timelyMapper.clear();
        logger.info("clear db timely hotKey, effect row : {}",row);
    }

}
