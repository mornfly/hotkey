package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


class NameUtils {

    private static AtomicLong providerId = new AtomicLong();

    private static AtomicLong serverId = new AtomicLong();

    private static AtomicLong consumerId = new AtomicLong();

    private static Map<Class, Map<String, String>> consumerBeanMap = new ConcurrentHashMap<Class, Map<String, String>>();

    static String checkConsumerBeanName(Class<?> consumerClazz, String alias) {
            Map<String, String> map = consumerBeanMap.get(consumerClazz);
            return map == null ? null : map.get(alias);
    }

    static String buildConsumerBeanName(Class<?> consumerClazz, String alias, String id) {
        String beanName;
        if (id == null || id.trim().isEmpty()) {
            beanName = lowerFirst(consumerClazz.getSimpleName() + "_CONSUMER_" + consumerId.incrementAndGet());
        } else {
            beanName = id;
        }
        addConsumerBeanName(consumerClazz, alias, beanName);
        return beanName;
    }

    private static void addConsumerBeanName(Class<?> consumerClazz, String alias, String beanName) {
        Map<String, String> map = consumerBeanMap.get(consumerClazz);
        if (map == null) {
            map = new ConcurrentHashMap<String, String>();
            consumerBeanMap.put(consumerClazz, map);
        }
        map.put(alias, beanName);
    }

    static String buildServerBeanName(Class<?> interfaceId) {
        return lowerFirst(interfaceId.getSimpleName() + "_SERVER_" + serverId.incrementAndGet());
    }

    static String buildProviderBeanName(Class<?> interfaceId) {
        return lowerFirst(interfaceId.getSimpleName() + "_JSF_" + providerId.incrementAndGet());
    }

    private static String lowerFirst(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

}
