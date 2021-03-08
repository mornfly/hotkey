package com.jd.platform.hotkey.common.model;

import com.jd.platform.hotkey.common.model.typeenum.KeyType;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * netty通信消息
 *
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class HotKeyMsg {
    private int magicNumber;

    private String appName;

    private MessageType messageType;

    private String body;

    private List<HotKeyModel> hotKeyModels;

    private List<KeyCountModel> keyCountModels;

    private InetSocketAddress address;

    public HotKeyMsg(MessageType messageType) {
        this(messageType, null);
    }

    public HotKeyMsg(MessageType messageType, String appName) {
        this.appName = appName;
        this.messageType = messageType;
    }

    public HotKeyMsg() {
    }

    public void protoToMsg(HotKeyMsgProto.HotKeyMsgWrapperProto proto, InetSocketAddress address) {
        if (proto == null) {
            return;
        }
        setMagicNumber(new Long(proto.getMagicNumber()).intValue());
        setAppName(proto.getAppName());
        setMessageType(MessageType.valueOf(proto.getMessageType().name()));
        setBody(proto.getBody());
        setAddress(address);
        List<HotKeyMsgProto.HotKeyMsgWrapperProto.HotKeyModel> keyModels = proto.getHotKeyModelsList();
        if (keyModels != null && keyModels.size() > 0) {
            List<HotKeyModel> hotKeyModels = new ArrayList<>();
            for (HotKeyMsgProto.HotKeyMsgWrapperProto.HotKeyModel model : keyModels) {
                HotKeyModel hotKeyModel = new HotKeyModel();
                hotKeyModel.setId(model.getId());
                hotKeyModel.setAppName(model.getAppName());
                hotKeyModel.setKeyType(KeyType.valueOf(model.getKeyType().name()));
                hotKeyModel.setKey(model.getKey());
                hotKeyModel.setRemove(model.getRemove());
                hotKeyModel.setCount(model.getCount());
                hotKeyModel.setCreateTime(model.getCreateTime());
                hotKeyModels.add(hotKeyModel);
            }
            setHotKeyModels(hotKeyModels);
        }
        List<HotKeyMsgProto.HotKeyMsgWrapperProto.KeyCountModel> keyCounts = proto.getKeyCountModelsList();
        if (keyCounts != null && keyCounts.size() > 0) {
            List<KeyCountModel> keyCountModels = new ArrayList<>();
            for (HotKeyMsgProto.HotKeyMsgWrapperProto.KeyCountModel keyCount : keyCounts) {
                KeyCountModel keyCountModel = new KeyCountModel();
                keyCountModel.setCreateTime(keyCount.getCreateTime());
                keyCountModel.setHotHitCount(keyCount.getHotHitCount());
                keyCountModel.setRuleKey(keyCount.getRuleKey());
                keyCountModel.setTotalHitCount(keyCount.getTotalHitCount());
                keyCountModels.add(keyCountModel);
            }
            setKeyCountModels(keyCountModels);
        }
    }

    public HotKeyMsgProto.HotKeyMsgWrapperProto msgToProtobuf() {

        HotKeyMsgProto.HotKeyMsgWrapperProto.Builder proto = HotKeyMsgProto.HotKeyMsgWrapperProto.newBuilder();
        proto.setMagicNumber(this.getMagicNumber());
        if (this.getAppName() == null){
            proto.setAppName("");
        }else {
            proto.setAppName(this.getAppName());
        }
        proto.setMessageType(HotKeyMsgProto.HotKeyMsgWrapperProto.MessageType.valueOf(this.getMessageType().name()));
        if (this.getBody() == null){
            proto.setBody("");
        }else {
            proto.setBody(this.getBody());
        }
        if (this.getHotKeyModels() != null && this.getHotKeyModels().size() > 0) {
            for (HotKeyModel model : this.getHotKeyModels()) {
                HotKeyMsgProto.HotKeyMsgWrapperProto.HotKeyModel.Builder hotkeyModel = HotKeyMsgProto.HotKeyMsgWrapperProto.HotKeyModel.newBuilder();
                hotkeyModel.setAppName(model.getAppName());
                hotkeyModel.setKeyType(HotKeyMsgProto.HotKeyMsgWrapperProto.HotKeyModel.KeyType.valueOf(model.getKeyType().name()));
                hotkeyModel.setRemove(model.isRemove());
                hotkeyModel.setCreateTime(model.getCreateTime());
                hotkeyModel.setKey(model.getKey());
                hotkeyModel.setCount(model.getCount());
                proto.addHotKeyModels(hotkeyModel.build());
            }
        }
        if (this.getKeyCountModels() != null && this.getKeyCountModels().size() > 0) {
            for (KeyCountModel model : this.getKeyCountModels()) {
                HotKeyMsgProto.HotKeyMsgWrapperProto.KeyCountModel.Builder keyCountModel = HotKeyMsgProto.HotKeyMsgWrapperProto.KeyCountModel.newBuilder();
                keyCountModel.setRuleKey(model.getRuleKey());
                keyCountModel.setTotalHitCount(model.getTotalHitCount());
                keyCountModel.setHotHitCount(model.getHotHitCount());
                keyCountModel.setCreateTime(model.getCreateTime());
                proto.addKeyCountModels(keyCountModel.build());
            }
        }
        return proto.build();
    }

    @Override
    public String toString() {
        return "HotKeyMsg{" +
                "magicNumber=" + magicNumber +
                ", appName='" + appName + '\'' +
                ", messageType=" + messageType +
                ", body='" + body + '\'' +
                ", hotKeyModels=" + hotKeyModels +
                ", keyCountModels=" + keyCountModels +
                '}';
    }

    public List<HotKeyModel> getHotKeyModels() {
        return hotKeyModels;
    }

    public void setHotKeyModels(List<HotKeyModel> hotKeyModels) {
        this.hotKeyModels = hotKeyModels;
    }

    public List<KeyCountModel> getKeyCountModels() {
        return keyCountModels;
    }

    public void setKeyCountModels(List<KeyCountModel> keyCountModels) {
        this.keyCountModels = keyCountModels;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
}
