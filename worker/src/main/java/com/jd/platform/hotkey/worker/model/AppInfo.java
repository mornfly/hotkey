package com.jd.platform.hotkey.worker.model;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
     * 某app的全部channel
     */
//    private ChannelGroup channelGroup;

    private Map<ChannelHandlerContext,InetSocketAddress> channelGroup;

    public AppInfo(String appName) {
        this.appName = appName;
        channelGroup = new ConcurrentHashMap<>();
    }

    public void groupPush(Object object) {
        for (Map.Entry<ChannelHandlerContext, InetSocketAddress> entry : channelGroup.entrySet()) {
            entry.getKey().writeAndFlush(new DatagramPacket((ByteBuf) object, entry.getValue()));
        }
    }

    public void add(ChannelHandlerContext ctx,InetSocketAddress address) {
        channelGroup.put(ctx,address);
    }

    public void remove(ChannelHandlerContext ctx) {
        channelGroup.remove(ctx);
    }

    public String getAppName() {
        return appName;
    }

    public int size() {
        return channelGroup.size();
    }

}
