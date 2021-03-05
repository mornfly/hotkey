package com.jd.platform.hotkey.worker.netty.client;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author wuweifeng wrote on 2019-12-13
 * @version 1.0
 */
public interface IClientChangeListener {
    /**
     * 发现新连接
     */
    void newClient(String appName, String channelId, ChannelHandlerContext ctx, InetSocketAddress address);

    /**
     * 客户端掉线
     */
    void loseClient(ChannelHandlerContext ctx);
}