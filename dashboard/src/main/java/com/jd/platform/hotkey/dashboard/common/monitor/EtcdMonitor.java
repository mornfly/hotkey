package com.jd.platform.hotkey.dashboard.common.monitor;

import cn.hutool.core.util.StrUtil;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.rule.KeyRule;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.ReceiveCountMapper;
import com.jd.platform.hotkey.dashboard.model.ReceiveCount;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import com.jd.platform.hotkey.dashboard.util.RuleUtil;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @ProjectName: hotkey
 * @ClassName: EtcdMonitor
 * @Author: liyunfeng31
 * @Date: 2020/4/18 18:29
 */
@SuppressWarnings("ALL")
@Component
public class EtcdMonitor {

    private static Logger log = LoggerFactory.getLogger(EtcdMonitor.class);

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private ChangeLogMapper logMapper;
    @Resource
    private WorkerService workerService;

    @Resource
    private DataHandler dataHandler;

    @Resource
    private ReceiveCountMapper receiveCountMapper;

//    @PostConstruct
//    public void init() {
//        CompletableFuture.runAsync(() -> {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            for (int i = 0; i < 200; i++) {
//                configCenter.put(ConfigConstant.hotKeyRecordPath + "abc/" + i, UUID.randomUUID().toString());
//            }
//        });
//    }

    /**
     * 监听新来的热key，该key的产生是来自于手工在控制台添加
     */
    public void watchHandOperationHotKey() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                EventWrapper eventWrapper = build(event);

                String appKey = event.getKv().getKey().toStringUtf8().replace(ConfigConstant.hotKeyPath, "");
                eventWrapper.setKey(appKey);
                dataHandler.offer(eventWrapper);
            }
        });
    }

    /**
     * 监听新来的热key，该key的产生是来自于worker集群推送过来的
     */
    public void watchHotKeyRecord() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyRecordPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                EventWrapper eventWrapper = build(event);

                String appKey = event.getKv().getKey().toStringUtf8().replace(ConfigConstant.hotKeyRecordPath, "");
                eventWrapper.setKey(appKey);

                dataHandler.offer(eventWrapper);
            }
        });
    }

    private EventWrapper build(Event event) {
        KeyValue kv = event.getKv();
        long ttl = configCenter.timeToLive(kv.getLease());
        String v = kv.getValue().toStringUtf8();
        Event.EventType eventType = event.getType();
        EventWrapper eventWrapper = new EventWrapper();
        eventWrapper.setValue(v);
        eventWrapper.setDate(new Date());
        eventWrapper.setTtl(ttl);
        eventWrapper.setVersion(kv.getVersion());
        eventWrapper.setEventType(eventType);
        eventWrapper.setUuid(v);
        return eventWrapper;
    }


    @PostConstruct
    public void startWatch() {
        //拉取rules
        fetchRuleFromEtcd();

        //规则拉取完毕后才能去开始入库
        dataHandler.insertRecords();

        //开始监听热key产生
        watchHotKeyRecord();

        watchHandOperationHotKey();

        //监听rule变化
        watchRule();

        watchWorkers();
    }

    /**
     * 异步监听rule规则变化
     */

    public void watchRule() {
        CompletableFuture.runAsync(() -> {
            try {
                KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.rulePath);
                //如果有新事件，即rule的变更，就重新拉取所有的信息
                while (watchIterator.hasNext()) {
                    //这句必须写，next会让他卡住，除非真的有新rule变更
                    WatchUpdate watchUpdate = watchIterator.next();
                    List<Event> eventList = watchUpdate.getEvents();

                    //全量拉取rule信息
                    fetchRuleFromEtcd();
                }
            } catch (Exception e) {
                log.error("watch rule err");
            }

        });
    }

    /**
     * 启动后从etcd拉取所有rule
     */
    private void fetchRuleFromEtcd() {
        RuleUtil.init();
        try {
            List<KeyRule> ruleList = new ArrayList<>();

            //从etcd获取rule
            List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.rulePath);

            for (KeyValue keyValue : keyValues) {
                try {
                    log.info(keyValue.getKey() + "---" + keyValue.getValue());
                    String appName = keyValue.getKey().toStringUtf8().replace(ConfigConstant.rulePath, "");
                    if (StrUtil.isEmpty(appName)) {
                        configCenter.delete(keyValue.getKey().toStringUtf8());
                        continue;
                    }
                    String rulesStr = keyValue.getValue().toStringUtf8();
                    RuleUtil.put(appName, FastJsonUtils.toList(rulesStr, KeyRule.class));
                } catch (Exception e) {
                    log.error("rule parse failure");
                }

            }

        } catch (StatusRuntimeException ex) {
            //etcd连不上
            log.error("etcd connected fail. Check the etcd address!!!");
        }

    }

    private void watchWorkers() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.workersPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                long version = kv.getModRevision();
                String uuid = k + Constant.JOIN + version;
                Worker worker = new Worker(k, v, uuid);
                if (eventType.equals(Event.EventType.PUT)) {
                    workerService.insertWorkerBySys(worker);
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    worker.setState(0);
                    workerService.updateWorker(worker);
                }
            }
        });
    }

    //@PostConstruct
    public void watchReceiveKeyCount() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.totalReceiveKeyCount);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                long version = kv.getModRevision();
                String uuid = k + Constant.JOIN + version;
                if (eventType.equals(Event.EventType.PUT)) {
                    receiveCountMapper.insert(new ReceiveCount(k, Long.parseLong(v), uuid));
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    receiveCountMapper.insert(new ReceiveCount(k, 0L, uuid));
                }
            }
        });
    }


    private Event event(KvClient.WatchIterator watchIterator) {
        return watchIterator.next().getEvents().get(0);
    }


}
