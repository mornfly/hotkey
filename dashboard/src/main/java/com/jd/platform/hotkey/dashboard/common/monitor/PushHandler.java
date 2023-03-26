package com.jd.platform.hotkey.dashboard.common.monitor;

import com.alibaba.fastjson.JSON;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.biz.service.UserService;
import com.jd.platform.hotkey.dashboard.common.domain.PushMsgWrapper;
import com.jd.platform.hotkey.dashboard.common.domain.vo.AppCfgVo;
import com.jd.platform.hotkey.dashboard.warn.dongdong.DongDongApiManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

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
     * app-config map
     */
    public static Map<String, AppCfgVo> appCfgMap = new ConcurrentHashMap<>();

    /**
     * app-time 用于app存储报警时间 做拦截
     */
    private static Map<String,Long> appIntervalMap = new ConcurrentHashMap<>();

    /**
     * 超过阈值的热点记录次数 放到队列里
     */
    private static final BlockingQueue<PushMsgWrapper> MSG_QUEUE = new LinkedBlockingQueue<>();


    @Resource
    private IConfigCenter configCenter;

    @Resource
    private UserService userService;

    @Resource
    private DongDongApiManager apiManager;

    /**
     * 监控和入队
     */
    public void monitorAndPush(String app) throws InterruptedException {
        AppCfgVo cfg = PushHandler.appCfgMap.get(app);
        if(cfg.getWarn().equals(1)){
            SlidingWindow wd = cfg.getWindow();
            int count = wd.addCount(1);
            if(count == 0){ return; }
            MSG_QUEUE.put(new PushMsgWrapper(app, count));
        }
    }


    /**
     * 启动线程处理警报消息队列
     */
    public void pushWarnMsg() {
        //  初始化MAP到内存
        initAppCfgMap();
        logger.info("initAppCfgMap success AppCfgMap:{}",JSON.toJSONString(appCfgMap));

        while (true){
            try {
                PushMsgWrapper msgWrapper = MSG_QUEUE.take();
                String warnApp = msgWrapper.getApp();
                Long msgTime = msgWrapper.getDate();
                boolean send = check(warnApp, msgTime);
                if(send){
                    Integer ct = msgWrapper.getCount();
                    String content = ct == -1 ? warnApp+"热点记录频率低于最小阈值，请注意！" : warnApp+"热点记录频率高于最大阈值，请注意！";
                    apiManager.push(TITLE,content);
                }
            } catch (InterruptedException e) {
                logger.info("pushWarnMsg thread error ,  msg :{}", e.getMessage());
            }
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


    /**
     * 初始化cfgMap和滑动窗口
     */
    private void initAppCfgMap() {
        /**
         * 为了加入最小值
         */
        configCenter.delete(ConfigConstant.appCfgPath);
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.appCfgPath);
        if(CollectionUtils.isEmpty(keyValues) || keyValues.size()==1){
            List<String> apps = userService.listApp();
            for (String ap : apps) {
                AppCfgVo cfg = new AppCfgVo(ap);
                appCfgMap.put(ap,cfg);
                configCenter.put(ConfigConstant.appCfgPath + ap, JSON.toJSONString(cfg));
            }
            return;
        }

        for (KeyValue keyValue : keyValues) {
            String val = keyValue.getValue().toStringUtf8();
            if(StringUtils.isNotEmpty(val)){
                AppCfgVo cfg = JSON.parseObject(val, AppCfgVo.class);
                appCfgMap.put(cfg.getApp(),cfg);
            }
        }
    }

}