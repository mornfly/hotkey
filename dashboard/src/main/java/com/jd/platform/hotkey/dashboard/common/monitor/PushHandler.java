package com.jd.platform.hotkey.dashboard.common.monitor;


import com.jd.platform.hotkey.dashboard.biz.mapper.UserMapper;
import com.jd.platform.hotkey.dashboard.warn.DongDongApiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: hotkey
 * @ClassName: PushHandler
 * @Description: 处理推送
 * @Author: liyunfeng31
 * @Date: 2020/9/3 10:47
 */

@Component
public class PushHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 报警标题
     */
    private static final String TITLE = "hotKey异常提醒";

    /**
     * 拦截重复报警间隔 10分钟
     */
    private static final long INTERVAL = 10*60*1000L;


    /**
     * app-time 用于app存储报警时间 做拦截
     */
    private static Map<String,Long> appIntervalMap = new ConcurrentHashMap<>();


    @Resource
    private DongDongApiManager apiManager;

    @Resource
    private UserMapper userMapper;


    /**
     * 发送消息
     */
    public void pushMsg(String app, Date msgTime, String content) {
        boolean send = check(app, msgTime.getTime());
        if(send){
            logger.info("Warn PushMsg content:{}",content);
            List<String> erpList = userMapper.listErpByApp(app);
            apiManager.push(TITLE,content,erpList);
        }
    }


    /**
     * 防止重复发送警报
     * @param warnApp app
     * @param msgTime time
     * @return result
     */
    private synchronized boolean check(String warnApp, Long msgTime){
        Long maxTime = appIntervalMap.get(warnApp);
        if(maxTime == null){
            appIntervalMap.put(warnApp,msgTime+INTERVAL);
            return true;
        }else{
            if(msgTime > maxTime){
                appIntervalMap.put(warnApp,msgTime+INTERVAL);
                return true;
            }
        }
        return false;
    }

}
