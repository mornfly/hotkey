package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;

import java.lang.annotation.*;

/**
 * Date: 2016/11/28 15:53
 * Email: wangyulie@jd.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface JsfParameter {
    /**
     * 参数配置关键字
     *
     * @return
     */
    String key();

    /**
     * 参数配置值
     *
     * @return
     */
    String keyValue();

    /**
     * 是否为隐藏配置。是的话，key自动加上"."作为前缀，且业务代码不能获取到，只能从filter里取到
     *
     * @return
     */
    boolean hide() default false;
}
