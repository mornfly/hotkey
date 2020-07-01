package com.jd.platform.hotkey.worker.counter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-28
 */
@Configuration
public class CounterConfig {
    /**
     * 队列
     */
    public static DelayQueue<KeyCountItem> DELAY_QUEUE = new DelayQueue<>();

    @Bean
    public CounterConsumer counterConsumer() {
        CounterConsumer counterConsumer = new CounterConsumer();
        counterConsumer.beginConsume();
        return counterConsumer;
    }

}
