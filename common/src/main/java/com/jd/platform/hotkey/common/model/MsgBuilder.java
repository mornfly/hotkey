package com.jd.platform.hotkey.common.model;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-04-22
 */
public class MsgBuilder {
//    public static ByteBuf buildByteBuf(String msg) {
//        return Unpooled.copiedBuffer((msg + Constant.DELIMITER).getBytes());
//    }

//    public static ByteBuf buildByteBuf(HotKeyMsg hotKeyMsg) {
//        byte[] bytes = ProtostuffUtils.serialize(hotKeyMsg);
//        byte[] delimiter = Constant.DELIMITER.getBytes();
//
//        byte[] bt3 = new byte[bytes.length + delimiter.length];
//        System.arraycopy(bytes, 0, bt3, 0, bytes.length);
//        System.arraycopy(delimiter, 0, bt3, bytes.length, delimiter.length);
//        return Unpooled.copiedBuffer(bt3);
//    }
}
