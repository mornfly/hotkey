package com.jd.platform.hotkey.client.netty;

import cn.hutool.core.collection.CollectionUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.jd.platform.hotkey.client.Context;
import com.jd.platform.hotkey.client.callback.ReceiveNewKeyEvent;
import com.jd.platform.hotkey.client.core.eventbus.EventBusCenter;
import com.jd.platform.hotkey.client.log.JdLogger;
import com.jd.platform.hotkey.client.netty.event.ChannelInactiveEvent;
import com.jd.platform.hotkey.common.model.*;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.Constant;
import com.jd.platform.hotkey.common.tool.ProtostuffUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author wuweifeng wrote on 2019-11-05.
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                //向服务端发送消息
                ctx.writeAndFlush(DatagramPacketBuilder.getDatagramPacket(new HotKeyMsg(MessageType.PING, Constant.PING), ctx));
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        JdLogger.info(getClass(), "channelActive:" + ctx.name());
        ctx.writeAndFlush(DatagramPacketBuilder.getDatagramPacket(new HotKeyMsg(MessageType.APP_NAME, Context.APP_NAME), ctx));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //断线了，可能只是client和server断了，但都和etcd没断。也可能是client自己断网了，也可能是server断了
        //发布断线事件。后续10秒后进行重连，根据etcd里的worker信息来决定是否重连，如果etcd里没了，就不重连。如果etcd里有，就重连
        notifyWorkerChange(ctx.channel());
    }

    private void notifyWorkerChange(Channel channel) {
        EventBusCenter.getInstance().post(new ChannelInactiveEvent(channel));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {
        try {
            HotKeyMsgProto.HotKeyMsgWrapperProto proto = HotKeyMsgProto.HotKeyMsgWrapperProto.parseFrom(packet.content().nioBuffer());
            if (proto == null) {
                return;
            }
            HotKeyMsg msg = new HotKeyMsg();
            msg.protoToMsg(proto, packet.sender());
            if (MessageType.PONG == msg.getMessageType()) {
                JdLogger.info(getClass(), "heart beat");
                return;
            }
            if (MessageType.RESPONSE_NEW_KEY == msg.getMessageType()) {
                JdLogger.info(getClass(), "receive new key : " + msg);
                if (CollectionUtil.isEmpty(msg.getHotKeyModels())) {
                    return;
                }
                for (HotKeyModel model : msg.getHotKeyModels()) {
                    EventBusCenter.getInstance().post(new ReceiveNewKeyEvent(model));
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            JdLogger.info(getClass(), "packet to proto error , " + e.getMessage());
        }


    }

}
