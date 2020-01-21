package com.jd.platform.client.netty;

import com.jd.platform.client.Context;
import com.jd.platform.client.core.eventbus.EventBusCenter;
import com.jd.platform.client.netty.event.ChannelInactiveEvent;
import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.MessageType;
import com.jd.platform.common.tool.Constant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuweifeng wrote on 2019-11-05.
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<HotKeyMsg> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private volatile boolean active = false;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.ALL_IDLE){
                //向服务端发送消息
                ctx.writeAndFlush(new HotKeyMsg(MessageType.PING, Constant.PING)) ;
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new HotKeyMsg(MessageType.APP_NAME, Context.APP_NAME));
        active = true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        active = false;
        //断线了，可能只是client和server断了，但都和etcd没断。也可能是client自己断网了，也可能是server断了
        //发布断线事件。后续10秒后进行重连，根据etcd里的worker信息来决定是否重连，如果etcd里没了，就不重连。如果etcd里有，就重连
        notifyWorkerChange(ctx.channel());
    }

    private void notifyWorkerChange(Channel channel) {
        EventBusCenter.getInstance().post(new ChannelInactiveEvent(channel));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HotKeyMsg msg) throws Exception {
        System.err.println(msg);
        if (MessageType.PONG == msg.getMessageType()) {
            logger.info("heart beat");
            return;
        }
    }

    public boolean isActive() {
        return active;
    }
}
