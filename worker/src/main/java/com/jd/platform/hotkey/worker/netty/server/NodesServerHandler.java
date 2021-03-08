package com.jd.platform.hotkey.worker.netty.server;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.HotKeyMsgProto;
import com.jd.platform.hotkey.common.tool.ProtostuffUtils;
import com.jd.platform.hotkey.worker.netty.client.IClientChangeListener;
import com.jd.platform.hotkey.worker.netty.filter.INettyMsgFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 这里处理所有netty事件。
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NodesServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    /**
     * 客户端状态监听器
     */
    private IClientChangeListener clientEventListener;
    /**
     * 请自行维护Filter的添加顺序
     */
    private List<INettyMsgFilter> messageFilters = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        try {
            HotKeyMsgProto.HotKeyMsgWrapperProto proto = HotKeyMsgProto.HotKeyMsgWrapperProto.parseFrom(packet.content().nioBuffer());
            if (proto == null){
                return;
            }
            HotKeyMsg msg = new HotKeyMsg();
            msg.protoToMsg(proto,packet.sender());
            for (INettyMsgFilter messageFilter : messageFilters) {
                boolean doNext = messageFilter.chain(msg, ctx);
                if (!doNext) {
                    return;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            logger.error("packet to proto error , " + e.getMessage());
        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        logger.error("some thing is error , " + cause.getMessage());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (clientEventListener != null) {
            clientEventListener.loseClient(ctx);
        }
        ctx.close();
        super.channelInactive(ctx);
    }

    public void setClientEventListener(IClientChangeListener clientEventListener) {
        this.clientEventListener = clientEventListener;
    }

    public void addMessageFilter(INettyMsgFilter iNettyMsgFilter) {
        if (iNettyMsgFilter != null) {
            messageFilters.add(iNettyMsgFilter);
        }
    }

    public void addMessageFilters(List<INettyMsgFilter> iNettyMsgFilters) {
        if (!CollectionUtils.isEmpty(iNettyMsgFilters)) {
            messageFilters.addAll(iNettyMsgFilters);
        }
    }
}
