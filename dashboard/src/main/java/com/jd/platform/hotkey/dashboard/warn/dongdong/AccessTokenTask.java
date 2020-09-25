package com.jd.platform.hotkey.dashboard.warn.dongdong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("accessTokenTask")
public class AccessTokenTask {
    @Autowired
    private DongDongApiManager dongDongApiManager;
    @Scheduled(cron = "0 0 0/1 ? * *")
    public void updateAccessToken(){
        dongDongApiManager.refreshAccessSignature();
    }
}