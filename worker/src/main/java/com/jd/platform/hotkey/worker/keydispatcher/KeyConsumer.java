package com.jd.platform.hotkey.worker.keydispatcher;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.jd.platform.hotkey.worker.keylistener.KeyEventOriginal;


import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import static com.jd.platform.hotkey.worker.keydispatcher.DispatcherConfig.QUEUE;
import static com.jd.platform.hotkey.worker.tool.InitConstant.totalDealCount;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-09
 */
public class KeyConsumer {


    private IKeyListener iKeyListener;

    private Queue<HotKeyModel> queue;


    public void setKeyListener(IKeyListener iKeyListener) {
        this.iKeyListener = iKeyListener;
    }

    public Queue<HotKeyModel> getQueue() {
        return queue;
    }

    public void setQueue(Queue<HotKeyModel> queue) {
        this.queue = queue;
    }

    public void beginConsume() {
        while (true) {
            HotKeyModel model = queue.poll();
            if (model == null) {
                continue;
            }
            if (model.isRemove()) {
                iKeyListener.removeKey(model, KeyEventOriginal.CLIENT);
            } else {
                iKeyListener.newKey(model, KeyEventOriginal.CLIENT);
            }

            //处理完毕，将数量加1
            totalDealCount.increment();


        }
    }
}
