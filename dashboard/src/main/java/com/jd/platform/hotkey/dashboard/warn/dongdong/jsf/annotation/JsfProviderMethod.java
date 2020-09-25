package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;

/**
 * Date: 2016/11/28 16:09
 * Email: wangyulie@jd.com
 */
public @interface JsfProviderMethod {
    /**
     * 方法名称（不支持重载方法）
     *
     * @return
     */
    String name();

    /**
     * 是否校验参数，支持JSR303，参见JSF用户手册#参数校验
     *
     * @return
     */
    boolean validation() default false;

    /**
     * 该方法的最大可并行执行请求数
     *
     * @return
     */
    int concurrents() default -1;

    /**
     * 是否启动Mock实现
     *
     * @return
     */
    boolean mock() default false;

    /**
     * 是否开启结果缓存。如果开启需要指定cacheref
     *
     * @return
     */
    boolean cache() default false;

    /**
     * 压缩算法（启动后是否压缩还取决于数据包大小）
     *
     * @return
     */
    String compress() default "";

    /**
     * 参数
     *
     * @return
     */
    JsfParameter[] parameters() default {};

}
