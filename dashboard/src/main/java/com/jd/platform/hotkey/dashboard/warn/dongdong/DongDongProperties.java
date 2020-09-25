package com.jd.platform.hotkey.dashboard.warn.dongdong;

import com.jd.platform.hotkey.dashboard.autoconfigure.AbstractProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dongdong")
public class DongDongProperties extends AbstractProperties {
    private String alias;
    private String token;
    private String timeout;
    private String appId;
    private String secret;
    private String noticeId;
}