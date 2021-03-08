package com.jd.platform.hotkey.common.model;

import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.Constant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * @Author: zs
 * @Date: 2021/3/6 10:19
 */
public class DatagramPacketBuilder {

    public static DatagramPacket getDatagramPacket(HotKeyMsg msg, ChannelHandlerContext ctx) {
        return getDatagramPacket(msg, ctx.channel());
    }

    public static DatagramPacket getDatagramPacket(HotKeyMsg msg, Channel ctx) {
        return getDatagramPacket(msg,(InetSocketAddress)ctx.remoteAddress());
    }

    public static DatagramPacket getDatagramPacket(HotKeyMsg msg) {
        return getDatagramPacket(msg,msg.getAddress());
    }

    public static DatagramPacket getDatagramPacket(HotKeyMsg msg,InetSocketAddress address) {
        return new DatagramPacket(MsgBuilder.buildProtobufByteBuf(msg),
                address);
    }
}
