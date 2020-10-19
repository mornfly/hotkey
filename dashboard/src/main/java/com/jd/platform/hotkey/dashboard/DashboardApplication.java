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
public class DashboardApplication{

    @Autowired
    private DongDongApiManager apiManager;

    public static void main(String[] args) {
        try {
            SpringApplication.run(DashboardApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
