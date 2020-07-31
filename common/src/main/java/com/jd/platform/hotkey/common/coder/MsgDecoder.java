package com.jd.platform.hotkey.common.coder;

import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.tool.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-07-29
 */
public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {
        try {

            byte[] body = new byte[in.readableBytes()];  //传输正常
            in.readBytes(body);

            list.add(ProtostuffUtils.deserialize(body, HotKeyMsg.class));

//            if (in.readableBytes() < 4) {
//                return;
//            }
//            in.markReaderIndex();
//            int dataLength = in.readInt();
//            if (dataLength < 0) {
//                channelHandlerContext.close();
//            }
//            if (in.readableBytes() < dataLength) {
//                in.resetReaderIndex();
//                return;
//            }
//
//            byte[] data = new byte[dataLength];
//            in.readBytes(data);
//
//            Object obj = ProtostuffUtils.deserialize(data, HotKeyMsg.class);
//            list.add(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
