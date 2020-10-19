package com.jd.platform.hotkey.dashboard.warn.dongdong;

import com.github.rholder.retry.*;
import com.jd.platform.hotkey.dashboard.biz.service.IBizAccessTokenService;
import com.jd.platform.hotkey.dashboard.model.BizAccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lihongliang32
 * @date 2020/5/20 5:53 下午
 */
@Service
public class DongDongApiManager {

    @Value("${dongdong.aspId:110.200.0000002.266}")
    private String aspId;

    @Value("${dongdong.secret:e2b05aa43959ddb6d37bd056b0243bc9}")
    private String secret;

    @Value("${dongdong.noticeId:~hotkey}")
    private String noticeId;

    @Resource
    private DongDongUtil dongUtil;


    private  Logger log = LoggerFactory.getLogger(getClass());


    private static final String VERSION="4.3";

    @Autowired
    private IBizAccessTokenService accessTokenService;

    public boolean refreshAccessSignature() {
        Callable<Boolean> callable = () -> {
            try {
                String accessToken = dongUtil.grant();
                List<BizAccessToken> tokens = accessTokenService.selectBizAccessTokenList(new BizAccessToken());
                if(tokens.size()>0){
                    BizAccessToken token = tokens.get(0);
                    token.setToken(accessToken);
                    token.setUpdatedBy("system");
                    token.setUpdatedTime(new Date());
                    log.info("updateBizAccessToken={}", accessToken);
                    accessTokenService.updateBizAccessToken(token);
                }else{
                    BizAccessToken token=new BizAccessToken();
                    token.setToken(accessToken);
                    token.setCreatedBy("system");
                    token.setCreatedTime(new Date());
                    log.info("insertBizAccessToken={}", accessToken);
                    accessTokenService.insertBizAccessToken(token);
                }
                return true;
            }catch (Exception e){
                return false;
            }
        };
        boolean result = false;
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.fibonacciWait(100, 2, TimeUnit.MINUTES))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .build();
        try {
            result = retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            log.error("DongdongOpenAPIManagerImpl::refreshAccessSignature Exception!", e);
        }
        return result;
    }


    public boolean push(String title, String content) {
        List<BizAccessToken> tokens = accessTokenService.selectBizAccessTokenList(new BizAccessToken());
        if(tokens.size() == 0){
            log.error("没有查询到有效的token！");
            return false;
        }
        String accessToken = tokens.get(0).getToken();
        if(StringUtils.isEmpty(accessToken)){
            return false;
        }
        try {
           return dongUtil.push(title,content,accessToken);
        }catch (Exception e){
            return false;
        }
    }



    public String getAspId() {
        return aspId;
    }

    public void setAspId(String aspId) {
        this.aspId = aspId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public static String getVERSION() {
        return VERSION;
    }
}