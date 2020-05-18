package com.jd.platform.hotkey.worker.model;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class AppInfo {
    /**
     * 应用名
     */
    private String appName;
    /**
     * 客户端ip 和 channel的映射关系
     */
    private Map<String, ChannelHandlerContext> map = new ConcurrentHashMap<>();

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", map=" + map +
                '}';
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, ChannelHandlerContext> getMap() {
        return map;
    }

    public void setMap(Map<String, ChannelHandlerContext> map) {
        this.map = map;
    }
}
