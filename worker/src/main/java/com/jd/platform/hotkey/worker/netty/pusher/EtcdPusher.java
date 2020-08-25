package com.jd.platform.hotkey.worker.netty.pusher;

import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.tool.HotKeyPathTool;
import com.jd.platform.hotkey.worker.rule.KeyRuleHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 将热key推送到etcd，dashboard监听后留做备份
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
@Component
public class EtcdPusher implements IPusher {
    @Resource
    private IConfigCenter iConfigCenter;

    /**
     * 将推送key到etcd的任务都丢到这个线程池里
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(128);

    @Override
    public void push(HotKeyModel model) {
        //2020-8-25日，新增线程池写入etcd的逻辑。本地测试发现8线程写入etcd，每秒最多500个key-value，128线程能写2500个以上
        EXECUTOR_SERVICE.submit(() -> {
            //推送到etcd，供dashboard监听入库。
            iConfigCenter.putAndGrant(HotKeyPathTool.keyRecordPath(model), UUID.randomUUID().toString(),
                    KeyRuleHolder.getRuleByAppAndKey(model).getDuration());
        });

    }

    @Override
    @Deprecated
    public void remove(HotKeyModel model) {
        //推送etcd删除
        iConfigCenter.delete(HotKeyPathTool.keyPath(model));
    }


}
