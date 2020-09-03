package com.jd.platform.hotkey.dashboard.common.monitor;

import com.alibaba.fastjson.JSON;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.biz.service.UserService;
import com.jd.platform.hotkey.dashboard.common.domain.PushMsgWrapper;
import com.jd.platform.hotkey.dashboard.common.domain.vo.AppCfgVo;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ProjectName: hotkey
 * @ClassName: PushHandler
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/9/3 10:47
 */
public class PushHandler {

    private static final long interval = 10*60*1000L;

    public static Map<String, AppCfgVo> appCfgMap = new ConcurrentHashMap<>();

    private static Map<String,Long> appIntervalMap = new ConcurrentHashMap<>();

    private static final BlockingQueue<PushMsgWrapper> MSG_QUEUE = new LinkedBlockingQueue<>();

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private UserService userService;


    /**
     * 入队
     */
    public void offer(PushMsgWrapper msgWrapper) {
        try {
            MSG_QUEUE.put(msgWrapper);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void pushWarnMsg() {
        initAppCfgMap();
        while (true){
            try {
                PushMsgWrapper msgWrapper = MSG_QUEUE.take();
                String warnApp = msgWrapper.getApp();
                Long msgTime = msgWrapper.getDate();
                boolean send = check(warnApp, msgTime);
                if(send){ doPush(); }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static synchronized boolean check(String warnApp, Long msgTime){
        Long maxTime = appIntervalMap.get(warnApp);
        if(maxTime == null){
            appIntervalMap.put(warnApp,msgTime+interval);
            return true;
        }else{
            if(msgTime > maxTime){
                appIntervalMap.put(warnApp,msgTime+interval);
                return true;
            }
        }
        return false;
    }

    private static void doPush() {
    }


    private void initAppCfgMap() {
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.appCfgPath);
        if(CollectionUtils.isEmpty(keyValues)){
            List<String> apps = userService.listApp();
            for (String ap : apps) {
                AppCfgVo cfg = new AppCfgVo(ap);
                appCfgMap.put(ap,cfg);
                configCenter.put(ConfigConstant.appCfgPath + ap, JSON.toJSONString(cfg));
            }
        }
    }

}
