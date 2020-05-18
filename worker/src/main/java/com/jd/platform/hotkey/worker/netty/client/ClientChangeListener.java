package com.jd.platform.hotkey.worker.netty.client;

import com.jd.platform.hotkey.worker.model.AppInfo;
import com.jd.platform.hotkey.worker.netty.holder.ClientInfoHolder;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 对客户端的管理，新来、断线的管理
 *
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class ClientChangeListener implements IClientChangeListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NEW_CLIENT = "监听到事件";
    private static final String NEW_CLIENT_JOIN = "new client join";
    private static final String CLIENT_LOSE = "client removed";

    /**
     * 客户端新增
     */
    @Override
    public synchronized void newClient(String appName, String ip, ChannelHandlerContext ctx) {
        logger.info(NEW_CLIENT);

        boolean appExist = false;
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            if (appName.equals(appInfo.getAppName())) {
                appExist = true;
                appInfo.getMap().put(ip, ctx);
                break;
            }
        }
        if (!appExist) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(appName);
            ClientInfoHolder.apps.add(appInfo);
            appInfo.getMap().put(ip, ctx);
        }

        logger.info(NEW_CLIENT_JOIN);
    }

    @Override
    public synchronized void loseClient(String ip) {
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            Map<String, ChannelHandlerContext> map = appInfo.getMap();
            if (map.containsKey(ip)) {
                map.remove(ip);
                break;
            }
        }
        logger.info(CLIENT_LOSE);
    }
}
