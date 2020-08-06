package com.jd.platform.hotkey.worker.keydispatcher;

import cn.hutool.core.date.SystemClock;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.tool.InitConstant;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static com.jd.platform.hotkey.worker.keydispatcher.DispatcherConfig.CONSUMERMAP;
import static com.jd.platform.hotkey.worker.keydispatcher.DispatcherConfig.MAPQUEUE;
import static com.jd.platform.hotkey.worker.keydispatcher.DispatcherConfig.QUEUE;
import static com.jd.platform.hotkey.worker.tool.InitConstant.expireTotalCount;
import static com.jd.platform.hotkey.worker.tool.InitConstant.totalOfferCount;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-09
 */
@Component
public class KeyProducer {

    // io线程与业务线程一一绑定
    Map<String, String> map = new ConcurrentHashMap<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);
    ReentrantLock lock = new ReentrantLock();

    public void push(HotKeyModel model, ChannelHandlerContext ctx) {
        if (model == null || model.getKey() == null) {
            return;
        }
        //5秒前的过时消息就不处理了
        if (SystemClock.now() - model.getCreateTime() > InitConstant.timeOut) {
            expireTotalCount.increment();
            return;
        }


        String threadId = ctx.channel().eventLoop().toString();
        lock.lock();
        try {
            if (map.containsKey(threadId)) {
                String index = map.get(threadId);
                MAPQUEUE.get(index).add(model);
                //通知
                CONSUMERMAP.get(MAPQUEUE.get(index)).emptyCondition.signal();
            } else {
                int index = atomicInteger.getAndIncrement();
                map.put(threadId, index + "");
                MAPQUEUE.get(index).add(model);
                //通知
                CONSUMERMAP.get(MAPQUEUE.get(index)).emptyCondition.signal();
            }


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            lock.unlock();
        }


        totalOfferCount.increment();
    }

}
