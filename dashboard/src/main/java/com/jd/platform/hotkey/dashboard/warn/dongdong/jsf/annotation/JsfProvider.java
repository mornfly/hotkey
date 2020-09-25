package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;

import com.jd.jsf.gd.util.Constants;

import java.lang.annotation.*;

/**
 * Date: 2016/11/3 12:03
 * Email: wangyulie@jd.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface JsfProvider {

    /**
     * alias名字, 不同的alias使用同一份server
     * 每一个 interfaceClass 都会有一份对应的 alias， 总共生成的provder个数是
     * interfaceClass.length + alias.length
     *
     * @return
     */
    String[] alias();

    /**
     * 接口类型
     * <p>
     * 不同的 interfaceClass 使用不同的server
     * <p/>
     * 默认单接口的类可以不指定，如果有多个接口， 请手工指定
     *
     * @return
     */
    Class<?>[] interfaceClass() default {};

    /**
     * 注册到的服务端，必填
     *
     * @return the server [ ]
     */
    Server[] server() default {@Server};

    /**
     * 过滤器实现链。 List<AbstractFilter>
     *
     * @return
     */
    String[] filterRef() default {};

    /**
     * 是否注册到注册中心
     *
     * @return the boolean
     */
    String register() default "true";

    /**
     * 是否动态发布服务
     *
     * @return the boolean
     */
    String dynamic() default "true";

    /**
     * 服务端权重
     *
     * @return the int
     */
    String weight() default Constants.DEFAULT_PROVIDER_WEIGHT + "";

}
