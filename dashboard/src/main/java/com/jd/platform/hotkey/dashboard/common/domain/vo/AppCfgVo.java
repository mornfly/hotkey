package com.jd.platform.hotkey.dashboard.common.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jd.platform.hotkey.dashboard.common.monitor.SlidingWindow;

import java.beans.Transient;
import java.io.Serializable;

/**
 * @ProjectName: hotkey
 * @ClassName: AppCfgVo
 * @Author: liyunfeng31
 * @Date: 2020/9/2 10:29
 */
public class AppCfgVo implements Serializable {

    private String app;

    /**
     * 数据保存时长
     */
    private Integer dataTtl;

    /**
     * 警报周期
     */
    private Integer warnPeriod;

    /**
     * 警报阈值
     */
    private Integer warnThreshold;

    /**
     * 警报开关 1开启 0关闭
     */
    private Integer warn;

    /**
     * 版本
     */
    private Long version;

    /*
     * 最后修改人
     */
    private String modifier;

    @JsonIgnore
    private SlidingWindow window;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Integer getDataTtl() {
        return dataTtl;
    }

    public void setDataTtl(Integer dataTtl) {
        this.dataTtl = dataTtl;
    }

    public Integer getWarnPeriod() {
        return warnPeriod;
    }

    public void setWarnPeriod(Integer warnPeriod) {
        this.warnPeriod = warnPeriod;
    }

    public Integer getWarnThreshold() {
        return warnThreshold;
    }

    public void setWarnThreshold(Integer warnThreshold) {
        this.warnThreshold = warnThreshold;
    }

    public Integer getWarn() {
        return warn;
    }

    public void setWarn(Integer warn) {
        this.warn = warn;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public SlidingWindow getWindow() {
        return window;
    }

    public void setWindow(SlidingWindow window) {
        this.window = window;
    }

    public AppCfgVo() {
    }

    public AppCfgVo(String app) {
        this.app = app;
        this.dataTtl = 30;
        this.warnPeriod = 10*60;
        this.warnThreshold = 1000;
        this.version = 0L;
        this.warn = 1;
        this.modifier = "SYSTEM";
        this.window = new SlidingWindow(warnPeriod/60,warnThreshold);
    }

}
