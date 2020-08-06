package com.jd.platform.hotkey.worker.keydispatcher;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.jd.platform.hotkey.worker.tool.CpuNum;
import com.lmax.disruptor.dsl.Disruptor;
import org.jctools.queues.SpscArrayQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-09
 */
@Configuration
public class DispatcherConfig {
    @Resource
    private IKeyListener iKeyListener;

    private ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    @Value("${thread.count}")
    private int threadCount;

    /**
     * 队列
     */
    public static BlockingQueue<HotKeyModel> QUEUE = new LinkedBlockingQueue<>(2000000);
    public static Map<String, Queue<HotKeyModel>> MAPQUEUE = new ConcurrentHashMap<>();

    static {
        //
        // https://www.jianshu.com/p/fde38db97318
        int nowCount = CpuNum.workerCount();
        int num = 2000000 / nowCount;
        for (int i = 0; i < nowCount; i++) {
            Queue<HotKeyModel> spscArrayQueue = new SpscArrayQueue<>(num);
            MAPQUEUE.put(i + "", spscArrayQueue);
        }
    }

    @Bean
    public Consumer consumer() {
        int nowCount = CpuNum.workerCount();
        //将实际值赋给static变量
        if (threadCount != 0) {
            nowCount = threadCount;
        }

        List<KeyConsumer> consumerList = new ArrayList<>();
        for (int i = 0; i < nowCount; i++) {
            KeyConsumer keyConsumer = new KeyConsumer();
            keyConsumer.setKeyListener(iKeyListener);
            keyConsumer.setQueue(MAPQUEUE.get(i+""));
            consumerList.add(keyConsumer);

            threadPoolExecutor.submit(keyConsumer::beginConsume);
        }
        return new Consumer(consumerList);
    }

    public static void main(String[] args) {
        Queue<String> spscArrayQueue = new SpscArrayQueue<>(16);
    }
}
