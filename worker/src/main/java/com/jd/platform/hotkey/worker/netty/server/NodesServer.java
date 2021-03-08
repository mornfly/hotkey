package com.jd.platform.hotkey.worker.netty.server;


import com.jd.platform.hotkey.common.model.HotKeyMsgProto;
import com.jd.platform.hotkey.worker.netty.client.IClientChangeListener;
import com.jd.platform.hotkey.worker.netty.filter.INettyMsgFilter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 该server用于给各个微服务实例连接用。
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NodesServer {
    private IClientChangeListener clientChangeListener;
    private List<INettyMsgFilter> messageFilters;

    public void startNettyServer(int port) throws Exception {
        //boss单线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    //出来网络io事件，如记录日志、对消息编解码等
                    .handler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully (1000, 3000, TimeUnit.MILLISECONDS);
            }));
            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            //do nothing
            System.out.println("netty stop");
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * handler类
     */
    private class ChildChannelHandler extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) {
            NodesServerHandler serverHandler = new NodesServerHandler();
            serverHandler.setClientEventListener(clientChangeListener);
            serverHandler.addMessageFilters(messageFilters);

            ch.pipeline()
                    .addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
                    .addLast("protobufDecoder",new ProtobufDecoder(HotKeyMsgProto.HotKeyMsgWrapperProto.getDefaultInstance()))
                    .addLast("frameEncoder",new LengthFieldPrepender(4))
                    .addLast("protobufEncoder",new ProtobufEncoder())
                    .addLast(serverHandler);
        }
    }

    public void setClientChangeListener(IClientChangeListener clientChangeListener) {
        this.clientChangeListener = clientChangeListener;
    }

    public void setMessageFilters(List<INettyMsgFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }

}
