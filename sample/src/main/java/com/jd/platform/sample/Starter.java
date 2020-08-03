package com.jd.platform.sample;

import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.client.ClientStarter;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author wuweifeng wrote on 2020-01-14
 * @version 1.0
 */
@Component
public class Starter {
    @Value("${etcd.server}")
    private String etcd;
    @Value("${spring.application.name}")
    private String appName;

    @Resource
    private IConfigCenter iConfigCenter;



    @PostConstruct
    public void init() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName).setEtcdServer(etcd).build();
        starter.startPipeline();

        List<KeyValue> list = iConfigCenter.getPrefix("/jd/workers/sample/host");
        for (KeyValue keyValue : list) {
            System.out.println(keyValue.getKey() + keyValue.getValue().toStringUtf8());
        }
    }

}
