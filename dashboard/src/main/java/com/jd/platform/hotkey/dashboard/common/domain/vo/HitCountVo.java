package com.jd.platform.hotkey.dashboard.common.domain.vo;


import java.io.Serializable;
import java.util.Date;


/**
 * @author liyunfeng31
 */
public class HitCountVo implements Serializable {

    private String rule;

    private String app;

    private Integer hitCount;

    private Integer totalCount;

    private Integer days;

    private Integer hours;

    private Integer minutes;

    private Integer seconds;

    private Date createTime;

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

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public static HitCountVoBuilder aHitCountVo() {
        return new HitCountVoBuilder();
    }


    public static final class HitCountVoBuilder {
        private String rule;
        private String app;
        private Integer hitCount;
        private Integer totalCount;
        private Integer days;
        private Integer hours;
        private Integer minutes;
        private Integer seconds;
        private Date createTime;

        private HitCountVoBuilder() {
        }

        public HitCountVoBuilder withRule(String rule) {
            this.rule = rule;
            return this;
        }

        public HitCountVoBuilder withApp(String app) {
            this.app = app;
            return this;
        }

        public HitCountVoBuilder withHitCount(Integer hitCount) {
            this.hitCount = hitCount;
            return this;
        }

        public HitCountVoBuilder withTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public HitCountVoBuilder withDays(Integer days) {
            this.days = days;
            return this;
        }

        public HitCountVoBuilder withHours(Integer hours) {
            this.hours = hours;
            return this;
        }

        public HitCountVoBuilder withMinutes(Integer minutes) {
            this.minutes = minutes;
            return this;
        }

        public HitCountVoBuilder withSeconds(Integer seconds) {
            this.seconds = seconds;
            return this;
        }

        public HitCountVoBuilder withCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public HitCountVo build() {
            HitCountVo hitCountVo = new HitCountVo();
            hitCountVo.setRule(rule);
            hitCountVo.setApp(app);
            hitCountVo.setHitCount(hitCount);
            hitCountVo.setTotalCount(totalCount);
            hitCountVo.setDays(days);
            hitCountVo.setHours(hours);
            hitCountVo.setMinutes(minutes);
            hitCountVo.setSeconds(seconds);
            hitCountVo.setCreateTime(createTime);
            return hitCountVo;
        }
    }
}