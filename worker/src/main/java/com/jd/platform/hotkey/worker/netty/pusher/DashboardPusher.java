package com.jd.platform.hotkey.worker.netty.pusher;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.netty.pusher.store.HotkeyTempStore;
import org.springframework.stereotype.Component;

/**
 * 将热key推送到dashboard供入库
 * @author wuweifeng
 * @version 1.0
 * @date 2020-08-31
 */
@Component
public class DashboardPusher implements IPusher {
    @Override
    public void push(HotKeyModel model) {
        HotkeyTempStore.push(model);
    }

    @Override
    public void remove(HotKeyModel model) {

    }
}
