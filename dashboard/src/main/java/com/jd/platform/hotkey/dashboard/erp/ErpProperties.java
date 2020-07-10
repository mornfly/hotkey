package com.jd.platform.hotkey.dashboard.erp;

import com.jd.platform.hotkey.dashboard.autoconfigure.AbstractProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * User:  fuxueliang
 * Date:  16/8/17
 * Email: fuxueliang@jd.com
 */
@Component
@ConfigurationProperties(prefix = "erp")
public class ErpProperties extends AbstractProperties {

    private String excludePath;

    private String loginUrl;

    private String ssoAppUrl;

    private String ssoAppKey;

    private String ssoAppToken;

    private Boolean enabled;

    public String getExcludePath() {
        return excludePath;
    }

    public void setExcludePath(String excludePath) {
        this.excludePath = excludePath;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getSsoAppUrl() {
        return ssoAppUrl;
    }

    public void setSsoAppUrl(String ssoAppUrl) {
        this.ssoAppUrl = ssoAppUrl;
    }

    public String getSsoAppKey() {
        return ssoAppKey;
    }

    public void setSsoAppKey(String ssoAppKey) {
        this.ssoAppKey = ssoAppKey;
    }

    public String getSsoAppToken() {
        return ssoAppToken;
    }

    public void setSsoAppToken(String ssoAppToken) {
        this.ssoAppToken = ssoAppToken;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
