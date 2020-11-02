package com.jd.platform.hotkey.dashboard.warn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author lihongliang32
 * @date 2020/5/20 5:53 下午
 */
@Service
public class DongDongApiManager {

    @Resource
    private DongDongUtil dongUtil;


    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 启动时获取token，每隔1小时刷新一次token
     */
    @Scheduled(cron = "0 0 * * * ?")
    @PostConstruct
    public void updateAccessToken() {
        try {
            String accessToken = dongUtil.grant();
            log.info("刷新token：" + accessToken);
            DongDongToken.TOKEN = accessToken;
        } catch (Exception e) {
            log.error("refreshAccessSignature error:", e);
        }
    }


    public boolean push(String title, String content, List<String> erpList) {
        String accessToken = DongDongToken.TOKEN;
        if (StringUtils.isEmpty(accessToken)) {
            return false;
        }
        try {
            return dongUtil.push(title, content, accessToken, erpList);
        } catch (Exception e) {
            return false;
        }
    }

}