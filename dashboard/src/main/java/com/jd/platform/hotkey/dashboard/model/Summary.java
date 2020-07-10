package com.jd.platform.hotkey.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author liyunfeng31
 */
public class Summary implements Serializable {

    private Integer id;

    private String indexName;

    private String rule;

    private String app;

    private Integer val1;

    private Integer val2;

    private BigDecimal val3;

    private Integer days;

    private Integer hours;

    private Integer minutes;

    private Integer seconds;

    private Integer bizType;

    private String uuid;

    private Date createTime;

    public Summary() {
    }

    public Summary(String indexName, String rule, String app, Integer totalCount, Integer hitCount, Integer days, Integer hours, Integer minutes, Integer seconds, Integer bizType, String uuid) {
        this.indexName = indexName;
        this.rule = rule;
        this.app = app;
        this.val1 = totalCount;
        this.val2 = hitCount;
        this.val3 = BigDecimal.ZERO;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.bizType = bizType;
        this.uuid = uuid;
        this.createTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Integer getVal1() {
        return val1;
    }

    public void setVal1(Integer val1) {
        this.val1 = val1;
    }

    public Integer getVal2() {
        return val2;
    }

    public void setVal2(Integer val2) {
        this.val2 = val2;
    }

    public BigDecimal getVal3() {
        return val3;
    }

    public void setVal3(BigDecimal val3) {
        this.val3 = val3;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}