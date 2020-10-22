package com.jd.platform.hotkey.dashboard.warn;

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

    @Resource
    private DongDongUtil dongUtil;


    private  Logger log = LoggerFactory.getLogger(getClass());


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


    public boolean push(String title, String content, List<String> erpList) {
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
            return dongUtil.push(title,content,accessToken, erpList);
        }catch (Exception e){
            return false;
        }
    }

}