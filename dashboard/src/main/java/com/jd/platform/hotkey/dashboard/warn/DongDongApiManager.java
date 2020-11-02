package com.jd.platform.hotkey.dashboard.warn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    @PostConstruct
    public void updateAccessToken() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                String accessToken = dongUtil.grant();
                log.info("刷新token：" + accessToken);
                DongDongToken.TOKEN = accessToken;
            } catch (Exception e) {
                log.error("refreshAccessSignature error:", e);
            }
        }, 0, 60, TimeUnit.MINUTES);

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