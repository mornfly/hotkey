package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;

import com.jd.jsf.gd.util.Constants;

import java.lang.annotation.*;

/**
 * Date: 2016/11/8 18:04
 * Email: wangyulie@jd.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Server {

    /**
     * 默认服务数量
     */
    int serverNum() default 1;

    /**
     * 协议名，必填
     *
     * @return the string
     */
    String protocol() default Constants.DEFAULT_PROTOCOL;

    /**
     * 主机地址，选填
     *
     * @return the string
     */
    String host() default "";

    /**
     * 端口地址，选填
     *
     * @return the int
     */
    int port() default Constants.DEFAULT_SERVER_PORT;

    /**
     * 业务线程池大小，选填
     *
     * @return the int
     */
    int threads() default Constants.DEFAULT_SERVER_BIZ_THREADS;

    /**
     * 线程池类型，选填
     *
     * @return the string
     */
    String threadpool() default Constants.THREADPOOL_TYPE_CACHED;

    /**
     * 参数
     *
     * @return
     */
    JsfParameter[] parameters() default {};

}
