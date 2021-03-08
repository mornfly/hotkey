package com.jd.platform.hotkey.worker.netty.filter;

import com.jd.platform.hotkey.common.model.DatagramPacketBuilder;
import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.MsgBuilder;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 心跳包处理
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
@Order(1)
public class HeartBeatFilter implements INettyMsgFilter {

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.PING == message.getMessageType()) {
            ctx.writeAndFlush(DatagramPacketBuilder.getDatagramPacket(new HotKeyMsg(MessageType.PONG),message.getAddress()));
            return false;
        }
        return true;

    }
}
