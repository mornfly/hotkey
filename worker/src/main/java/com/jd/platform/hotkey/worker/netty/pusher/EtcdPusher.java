package com.jd.platform.hotkey.worker.netty.pusher;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Queues;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.tool.HotKeyPathTool;
import com.jd.platform.hotkey.worker.rule.KeyRuleHolder;
import com.jd.platform.hotkey.worker.tool.AsyncPool;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * 将热key推送到etcd，dashboard监听后留做备份
 *
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
@Component
public class EtcdPusher implements IPusher {
    @Resource
    private IConfigCenter iConfigCenter;

    private static LinkedBlockingQueue<HotKeyModel> hotKeyStoreQueue = new LinkedBlockingQueue<>();

    @Override
    public void push(HotKeyModel model) {
        hotKeyStoreQueue.offer(model);
    }

    @PostConstruct
    public void uploadToEtcd() {
        Map<Integer, List<String>> map = new HashMap<>();
        AsyncPool.asyncDo(() -> {
            while (true) {
                try {
                    //要么key达到1千个，要么达到1秒，就汇总上报给etcd一次
                    List<HotKeyModel> tempModels = new ArrayList<>();
                    Queues.drain(hotKeyStoreQueue, tempModels, 1000, 1, TimeUnit.SECONDS);
                    if (CollectionUtil.isEmpty(tempModels)) {
                        continue;
                    }
                    //区分出来不同的duration，不同的duration往etcd放时方法不一样
                    for (int i = 0; i < tempModels.size(); i++) {
                        HotKeyModel hotKeyModel = tempModels.get(i);
                        int duration = KeyRuleHolder.getRuleByAppAndKey(hotKeyModel).getDuration();
                        //取到value
                        List<String> keys = map.computeIfAbsent(duration, s -> new ArrayList<>());
                        //将该model的key存进去
                        keys.add(HotKeyPathTool.keyRecordPath(hotKeyModel));
                    }

                    for (Integer duration : map.keySet()) {
                        List<String> list = map.get(duration);
                        if (CollectionUtil.isEmpty(list)) {
                            continue;
                        }
                        Joiner joiner = Joiner.on(",");
                        String s = joiner.join(list);
                        iConfigCenter.putAndGrant(ConfigConstant.hotKeyBatchRecordPath + s, UUID.randomUUID().toString(), duration);
                        map.get(duration).clear();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    @Deprecated
    public void remove(HotKeyModel model) {
        //推送etcd删除
        iConfigCenter.delete(HotKeyPathTool.keyPath(model));
    }


}
