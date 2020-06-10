package com.jd.platform.hotkey.worker.disruptor;

import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventConsumer;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventProducer;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.jd.platform.hotkey.worker.tool.CpuNum;
import com.jd.platform.hotkey.worker.tool.InitConstant;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Disruptor创建producer的地方
 *
 * @author wuweifeng wrote on 2019-11-5.
 */
public class ProducerFactory {

    /**
     * 创建热key的producer
     *
     * @return HotKeyEventProducer
     */
    public static MessageProducer<HotKeyEvent> createHotKeyProducer(IKeyListener iKeyListener) {
        int threadCount = CpuNum.workerCount();
        //如果手工指定了线程数，就用手工指定的
        if (InitConstant.threadCount != 0) {
            threadCount = InitConstant.threadCount;
        } else {
            if (threadCount >= 8) {
                threadCount = threadCount / 2;
            }
        }

        HotKeyEventConsumer[] array = new HotKeyEventConsumer[threadCount];
        for (int i = 0; i < threadCount; i++) {
            array[i] = new HotKeyEventConsumer(i);
            array[i].setKeyListener(iKeyListener);
        }
        DisruptorBuilder<HotKeyEvent> builder = new DisruptorBuilder<>();
        Disruptor<HotKeyEvent> disruptor = builder
                .setBufferSize(InitConstant.bufferSize * 1024 * 1024)
                .setEventFactory(HotKeyEvent::new)
//                .setEventHandlers(array)   //重复消费的
                .setWorkerHandlers(array)    //不重复消费的
                .build();

        return new HotKeyEventProducer(disruptor);
    }

}
