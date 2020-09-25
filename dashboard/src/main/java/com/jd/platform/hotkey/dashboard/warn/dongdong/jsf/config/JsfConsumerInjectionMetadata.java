package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.config;

import org.springframework.beans.factory.annotation.InjectionMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2016/11/15 11:17
 * Email: wangyulie@jd.com
 */
class JsfConsumerInjectionMetadata {

    private Map<Class, InjectionMetadata> clazzMap = new HashMap<Class, InjectionMetadata>();

    void register(Class<?> clazz, InjectionMetadata injectionMetadata) {
        this.clazzMap.put(clazz, injectionMetadata);
    }


    InjectionMetadata findMetadata(Class<?> clazz) {
        return clazzMap.get(clazz);
    }

}
