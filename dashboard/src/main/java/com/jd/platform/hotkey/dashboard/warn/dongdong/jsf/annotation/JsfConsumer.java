package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;



import java.lang.annotation.*;

/**
 * Date: 2016/11/9 12:30
 * @author: wangyulie@jd.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsfConsumer {

    /**
     * Spring的BeanId
     *
     * @return
     */
    String id() default "";

    /**
     * 服务别名分组信息
     *
     * @return the string
     */
    String alias();

    /**
     * 调用的协议
     *
     * @return the string
     */
    String protocol() default com.jd.jsf.gd.util.Constants.DEFAULT_PROTOCOL;

    /**
     * 直连地址，配置了此地址就不再从注册中心获取。参见：JSF客户端用户手册#直连调用
     *
     * @return the string
     */
    String url() default "";

    /**
     * 过滤器实现链。 List<AbstractFilter>
     *
     * @return
     */
    String[] filterRef() default {};

    /**
     * 是否注册到注册中心
     *
     * @return
     */
    String register() default "true";

    /**
     * 是否从注册中心订阅
     *
     * @return
     */
    String subscribe() default "true";

    /**
     * 调用超时，选填
     *
     * @return the int
     */
    String timeout() default "5000";


    /**
     * 失败后重试次数，选填
     *
     * @return the int
     */
    int retries() default com.jd.jsf.gd.util.Constants.DEFAULT_RETRIES_TIME;

    /**
     * 序列化方式
     *
     * @return the string
     */
    String serialization() default com.jd.jsf.gd.util.Constants.DEFAULT_CODEC;

    /**
     * 参数
     *
     * @return
     */
    JsfParameter[] parameters() default {};

}
