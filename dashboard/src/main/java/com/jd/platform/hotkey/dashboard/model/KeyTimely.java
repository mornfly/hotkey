package com.jd.platform.hotkey.dashboard.model;


import java.io.Serializable;
import java.util.Date;

public class KeyTimely implements Serializable {


    private Long id;

    private String key;

    private String appName;

    private String val;

    /**
     * 缓存时间
     */
    private Long duration;

    private Date createTime;

    private String uuid;
    /**
     * 该rule的描述
     */
    private transient String ruleDesc;


    private String updater;


    public KeyTimely() {
    }

    public KeyTimely(String key, String updater) {
        this.key = key;
        this.updater = updater;
    }


    public KeyTimely(String key, String val, String appName, Long duration, String uuid, Date createTime) {
        this.key = key;
        this.val = val;
        this.uuid = uuid;
        this.appName = appName;
        this.duration = duration;
        this.createTime = createTime;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}