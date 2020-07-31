package com.jd.platform.sample;

import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.client.ClientStarter;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${total.count}")
    private int count;
    @Value("${total.sleep}")
    private int sleep;
    @Value("${total.period}")
    private long period;

    @Resource
    private IConfigCenter iConfigCenter;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName).setEtcdServer(etcd).setPushPeriod(period).build();
        starter.startPipeline();

        List<KeyValue> list = iConfigCenter.getPrefix("/jd/workers/sample/host");
        for (KeyValue keyValue : list) {
            System.out.println(keyValue.getKey() + keyValue.getValue().toStringUtf8());
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        new Thread(() -> {
//            long i1 = 0;
//            while (true) {
//                if (JdHotKeyStore.getValue(i1 + "") == null) {
//                    i1++;
//                    if (i1 % count == 0) {
//                        try {
//                            Thread.sleep(sleep);
//                            logger.info("number: " + i1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//        }).start();
    }

}
