package com.jd.platform.hotkey.dashboard.common.domain;

import cn.hutool.core.date.SystemClock;

import java.io.Serializable;

/**
 * 报警消息包装类，用于保存事件最准确的时间
 */
public class PushMsgWrapper implements Serializable {

    private String app;

    private Long date;

    private String msg;

    public PushMsgWrapper(String app) {
        this.app = app;
        this.date = SystemClock.now();
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}


