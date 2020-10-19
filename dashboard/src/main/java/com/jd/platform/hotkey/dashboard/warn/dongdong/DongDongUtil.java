package com.jd.platform.hotkey.dashboard.warn.dongdong;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.jd.dd.open.gw.api.domain.AccessSignatureResult;
import com.jd.dd.open.gw.api.domain.MessagePushResult;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author liyunfeng31
 */
@Component
public class DongDongUtil {

        @Value("${dongdong.aspId:110.200.0000002.266}")
        private String aspId;

        @Value("${dongdong.secret:e2b05aa43959ddb6d37bd056b0243bc9}")
        private String secret;

        @Value("${dongdong.noticeId:~hotkey}")
        private String noticeId;


        private final String VERSION="4.3";


        public String grant() throws Exception{
            String dogUrl = "http://dog-ee.jd.com/grant";
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("aspid", aspId));
            params.add(new BasicNameValuePair("secret", secret));
            params.add(new BasicNameValuePair("version", VERSION));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(dogUrl);
            //传参
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = httpclient.execute(httpPost);
            String res = EntityUtils.toString(response.getEntity());

            //返回结果对象AccessSignatureResult
            AccessSignatureResult result = JSON.parseObject(res, AccessSignatureResult.class);
            if (result.getCode() == 230031) {
                //请求成功
                return result.getAccessToken();
            }
            else {
                //失败操作
                return String.valueOf(result.getCode());
            }

        }

        public boolean push(String title,String content,String accessToken) throws Exception{
            String dogUrl = "http://dog-ee.jd.com/push";
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("accessid", UUID.randomUUID().toString()));
            params.add(new BasicNameValuePair("accessToken", accessToken));
            params.add(new BasicNameValuePair("aspid", aspId));//需要申请
            params.add(new BasicNameValuePair("version", VERSION));
            params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));

            //消息相关参数
            JSONObject json = new JSONObject();
            json.put("type", "notice_message");
            json.put("ver", VERSION);
            json.put("title", title);
            json.put("content", content);
            json.put("noticeId", noticeId);
            json.put("toTerminal", 7);
            json.put("sla", 0);
            json.put("app", "ee");
            //接收方
            List<String> pins = new ArrayList<>();
            pins.add("wuweifeng10");
            pins.add("liyunfeng31");
            pins.add("erp");
            json.put("tos", pins);

            JSONObject extend = new JSONObject();
            extend.put("url", "http://hotkey.jd.com");
            extend.put("pic", "https://img14.360buyimg.com/imagetools/jfs/t1/144156/7/8194/7263/5f5b34baEdfd90a49/a15144ab447bfbf2.png");
            json.put("extend", extend);
            params.add(new BasicNameValuePair("jsonMsg",JSON.toJSONString(json)));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(dogUrl);
            //传参
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String res = EntityUtils.toString(response.getEntity());
            System.out.println(""+ JSON.toJSONString(res));
            //返回结果对象AccessSignatureResult
            MessagePushResult result = JSON.parseObject(res, MessagePushResult.class);
            return result.getCode() == 230070;
        }

}
