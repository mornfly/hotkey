package com.jd.platform.hotkey.common.model;

import com.jd.platform.hotkey.common.tool.Constant;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.common.tool.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-04-22
 */
public class MsgBuilder {
    public static ByteBuf buildByteBuf(String msg) {
        return Unpooled.copiedBuffer((msg + Constant.DELIMITER).getBytes());
    }

    public static ByteBuf buildByteBuf(HotKeyMsg hotKeyMsg) {
        return Unpooled.copiedBuffer((FastJsonUtils.convertObjectToJSON(hotKeyMsg) + Constant.DELIMITER).getBytes());
    }

    public static ByteBuf buildProtobufByteBuf(HotKeyMsg hotKeyMsg) {
        return Unpooled.copiedBuffer(hotKeyMsg.msgToProtobuf().toByteArray());
    }
}
