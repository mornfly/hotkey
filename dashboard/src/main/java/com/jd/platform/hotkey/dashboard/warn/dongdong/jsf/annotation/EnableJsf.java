package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation;

import com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.config.JsfComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(JsfComponentRegistrar.class)
public @interface EnableJsf {
    /**
     * 注册中心 i.jsf.jd.com
     *
     * @return
     */
    String index() default "";

    /**
     * 设置 {@link #tokenFile()} 到 security.token.client.fileUrl
     * <p>
     * 是否开启消费端认证
     *  true|false
     * @return
     */
    String isConsumerOpenSecurity() default "";

    /**
     * 设置 {@link #tokenFile()} 到 security.token.server.fileUrl
     * <p>
     * 是否开启服务端认证
     * true|false
     * @return
     */
    String isProviderOpenSecurity() default "";

    /**
     * token 文件
     *
     * @return
     */
    String tokenFile() default "";

    /**
     * security.isOpen.provider 的值 <p/>
     * 0 关闭 <p/>
     * 1 只监听，所有服务使用者都可访问该服务 <p/>
     * 2 对未授权的服务使用者进行阻断 <p/>
     *
     * @return
     */
    String providerSecurityLevel() default "";
}
