package com.jd.platform.hotkey.worker.tool;

import io.netty.util.internal.PlatformDependent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-10-22
 */
@Component
public class DirectMemoryReporter {
    private static final String BUSINESS_KEY = "netty_direct_memory";
    private AtomicLong directMemory;

    @PostConstruct
    public void init(){
        //通过反射拿到netty进行堆外内存统计字段
        Field field = ReflectionUtils.findField(PlatformDependent.class,"DIRECT_MEMORY_COUNTER");
        field.setAccessible(true);
        try{
            directMemory = ((AtomicLong)field.get(PlatformDependent.class));
        }catch (Exception e){

        }
    }

    public void doReport(String processName){
        try{
            long memoryInb = directMemory.get();
        }catch (Exception e){

        }
    }
}