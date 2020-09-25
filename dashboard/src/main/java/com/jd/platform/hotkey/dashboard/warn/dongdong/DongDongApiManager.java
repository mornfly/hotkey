package com.jd.platform.hotkey.dashboard.warn.dongdong;

import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.jd.dd.open.gw.api.GrantService;
import com.jd.dd.open.gw.api.MessagePushService;
import com.jd.dd.open.gw.api.domain.*;
import com.jd.fastjson.JSON;
import com.jd.jsf.gd.util.DateUtils;
import com.jd.platform.hotkey.dashboard.biz.service.IBizAccessTokenService;
import com.jd.platform.hotkey.dashboard.model.BizAccessToken;
import com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation.JsfConsumer;
import com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation.JsfParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lihongliang32
 * @date 2020/5/20 5:53 下午
 */
@Slf4j
@Service("dongdongAPIManager")
public class DongDongApiManager {
    @JsfConsumer(id="dongdongGrantService",alias="${dongdong.alias}", timeout="${dongdong.timeout}",
            parameters= { @JsfParameter(key="token", keyValue="${dongdong.token}", hide=true)})
    private GrantService grantService;

    @JsfConsumer(id="dongdongMessagePushService",alias="${dongdong.alias}", timeout="${dongdong.timeout}",
            parameters= { @JsfParameter(key="token", keyValue="${dongdong.token}", hide=true)})
    private MessagePushService messagePushService;

    @Value("${jsf.dongdong.aspId}")
    private String aspId;

    @Value("${jsf.dongdong.secret}")
    private String secret;

    @Value("${jsf.dongdong.noticeId}")
    private String noticeId;

    @Autowired
    private IBizAccessTokenService accessTokenService;

    public boolean refreshAccessSignature() {
        Callable<Boolean> callable = () -> {
            AppSigInfo appSigInfo = new AppSigInfo();
            appSigInfo.setAspid(aspId);
            appSigInfo.setSecret(secret);
            appSigInfo.setVersion("4.3");
            AccessSignatureResult accessSignatureResult = this.grantService.refreshAccessSignature(appSigInfo);
            if (accessSignatureResult.getCode() == DDOpenAPIResultCodeEnum.MSG_SIGN_SUCCESS.getCode()) {
                String accessToken = accessSignatureResult.getAccessToken();
                //请求成功
                log.info("DongdongOpenAPIManagerImpl::refreshAccessSignature success! accessToken={}", accessToken);
                try{
                    List<BizAccessToken> tokens = accessTokenService.selectBizAccessTokenList(new BizAccessToken());
                    if(tokens.size()>0){
                        BizAccessToken token = tokens.get(0);
                        token.setToken(accessToken);
                        token.setUpdatedBy("system");
                        token.setUpdatedTime(new Date());
                        log.info("updateBizAccessToken={}", accessToken);

                       // accessTokenService.updateBizAccessToken(token);
                    }else{
                        BizAccessToken token=new BizAccessToken();
                        token.setToken(accessToken);
                        token.setCreatedBy("system");
                        token.setCreatedTime(new Date());
                        log.info("insertBizAccessToken={}", accessToken);

                        //   accessTokenService.insertBizAccessToken(token);
                    }
                }catch (Exception e){
                    log.error("accessToken保存失败："+e.getMessage());
                    return false;
                }
                return true;
            }
            log.error("DongdongOpenAPIManagerImpl::refreshAccessSignature failure! code={}, reason={}", accessSignatureResult.getCode(), accessSignatureResult.getErrmsg());
            return false;
        };
        boolean result = false;
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
//            .retryIfResult(Predicates.alwaysFalse())
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.fibonacciWait(100, 2, TimeUnit.MINUTES))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .build();
        try {
            result = retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            // It will stop after attempting to retry 3 times and throw a RetryException
            log.error("DongdongOpenAPIManagerImpl::refreshAccessSignature Exception!", e);
        }
        return result;
    }

    public boolean push(String title, String content, List<String> erps, String extendStr) {
        boolean result;
        try {
            List<BizAccessToken> tokens = accessTokenService.selectBizAccessTokenList(new BizAccessToken());
            if(tokens.size() == 0){
                log.error("没有查询到有效的token！");
                return false;
            }
            String accessToken = tokens.get(0).getToken();
            if (StringUtils.isNotBlank(accessToken)) {
                AccessSignature as = new AccessSignature();
                as.setAccessid(UUID.randomUUID().toString());
                as.setAccessToken(accessToken);//授权后获取的字段
                as.setAspid(aspId);//需要申请，与授权使用相同aspid
                as.setVersion("4.3");
                as.setTimestamp(System.currentTimeMillis());

                //构造消息参数
                Message msg = new Message();
                JSONObject json = new JSONObject();
                json.put("type", "notice_message");
                json.put("ver", "4.3");
                json.put("title", title);//长度限制50
                json.put("content", content);//目前暂不支持换行，长度限制1500
                json.put("noticeId", noticeId);// ~加原来的应用标识  如原来的 ump -> ~ump，申请邮件反馈中会给出相应的noticeId
                json.put("toTerminal", 7);//企业版支持(推所有终端传 7 ，非所有端请联系我们确认)
                json.put("sla", 1);//是否需要离线投递，申请通知时需要配置,0在线投递，1离线投递
                json.put("app", "ee");//代表发送的用户群体，ee代表国内ERP，泰国th.ee
                json.put("tos", erps);//一次最大500个

                JSONObject extend = new JSONObject();
                if(StringUtils.isEmpty(extendStr)) {
                    extend.put("url", "https://fangzhou.jd.com");//点击通知可跳转的url，url中含有中文时，请将中文进行urlencode编码
                }else{
                    extend.put("url", extendStr);//点击通知可跳转的url，url中含有中文时，请将中文进行urlencode编码
                }
                //extend.put("pic", pic);//图片暂不支持
                json.put("extend", extend);

//                json.put("extend", extend);
                msg.setJsonMsg(json.toJSONString());
                MessagePushResult messagePushResult = messagePushService.push(as, msg);
                if (messagePushResult.getCode() == 230070) {
                    //请求成功
                    log.info("DongdongOpenAPIManagerImpl::push success. to erps={}", JSON.toJSONString(erps));
                    result =  true;
                } else {
                    //失败操作
                    log.error("DongdongOpenAPIManagerImpl::push can not push message.code={} reason={}", messagePushResult.getCode(), messagePushResult.getErrmsg());

                    result= false;
                }
            } else {
                log.error("DongdongOpenAPIManagerImpl::push can not get dongdong.accessToken from JimDB");
                result =  false;
            }
        } catch (Exception e) {
            log.error("DongdongOpenAPIManagerImpl::push Exception happened!", e);
            result = false;
        }
        return result;
    }
}