package com.jd.platform.hotkey.worker.config;

import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Configuration
public class DisruptorConfig {
    @Resource
    private IKeyListener iKeyListener;

    @Value("${open.timeout}")
    private boolean openTimeOut;
    @Value("${thread.count}")
    private int threadCount;

//    @Bean
//    public MessageProducer<HotKeyEvent> messageProducer() {
//        InitConstant.openTimeOut = openTimeOut;
//        //将实际值赋给static变量
//        InitConstant.threadCount = threadCount;
//
//        return ProducerFactory.createHotKeyProducer(iKeyListener);
//    }


}
