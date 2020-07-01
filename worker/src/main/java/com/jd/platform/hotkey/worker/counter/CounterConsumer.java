package com.jd.platform.hotkey.worker.counter;

import com.jd.platform.hotkey.common.model.KeyCountModel;

import java.util.List;

import static com.jd.platform.hotkey.worker.counter.CounterConfig.DELAY_QUEUE;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-28
 */
public class CounterConsumer {

    public void beginConsume() {
        while (true) {
            try {
                KeyCountItem item = DELAY_QUEUE.take();
                List<KeyCountModel> keyCountModels = item.getList();
                String appName = item.getAppName();
                for (KeyCountModel keyCountModel : keyCountModels) {
                    String ruleKey = keyCountModel.getRuleKey();
                    int totalHitCount = keyCountModel.getTotalHitCount();
                    int hotHitCount = keyCountModel.getHotHitCount();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
