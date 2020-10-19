package com.jd.platform.hotkey.dashboard.warn.dongdong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liyunfeng31
 */
@Component("accessTokenTask")
public class AccessTokenTask {

    @Resource
    private DongDongApiManager apiManager;


    private Logger log = LoggerFactory.getLogger(getClass());


    @Scheduled(cron = "0 0 0/1 ? * *")
    public void updateAccessToken(){
        try {
            apiManager.refreshAccessSignature();
        }catch (Exception e){
            log.error("refreshAccessSignature error:",e);
        }
    }
}