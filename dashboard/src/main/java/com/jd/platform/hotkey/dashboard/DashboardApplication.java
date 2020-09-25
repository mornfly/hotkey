package com.jd.platform.hotkey.dashboard;

import com.jd.platform.hotkey.dashboard.common.monitor.PushHandler;
import com.jd.platform.hotkey.dashboard.warn.dongdong.DongDongApiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@EnableAsync
@EnableScheduling
@SpringBootApplication
public class DashboardApplication implements CommandLineRunner {

    @Autowired
    private DongDongApiManager apiManager;

    public static void main(String[] args) {
        try {
            SpringApplication.run(DashboardApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Resource
    PushHandler pushHandler;


    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(5000);
       // pushHandler.monitorAndPush("test");
        apiManager.refreshAccessSignature();
        Thread.sleep(1000);
        List<String> list = new ArrayList<>();
        list.add("liyunfeng31");
        list.add("wuweifeng10");
      //  String title, String content, List<String> erps, String extendStr) {
        apiManager.push("title","this is content",list,"this is extend");
    }
}
