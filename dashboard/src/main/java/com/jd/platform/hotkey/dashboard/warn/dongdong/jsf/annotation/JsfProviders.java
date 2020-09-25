package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;

import java.lang.annotation.*;

/**
 * Date: 2016/11/3 22:30
 * Email: wangyulie@jd.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JsfProviders {
    JsfProvider[] value();

    /**
     * 参数
     *
     * @return
     */
    JsfParameter[] parameters() default {};

    /**
     * 方法设置
     * @return
     */
    JsfProviderMethod[] methods() default {};
}
