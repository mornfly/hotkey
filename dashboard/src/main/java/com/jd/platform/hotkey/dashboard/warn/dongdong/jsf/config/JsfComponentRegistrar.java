package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.config;

import com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation.EnableJsf;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 * Date: 2018-12-26 14:30
 * Email: wangyulie@jd.com
 */
public class JsfComponentRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerJsfBeanFactoryPostProcessor(metadata, registry);
        registerJsfConsumerAnnotationBeanPostProcessor(registry);
    }

    private void registerJsfConsumerAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {
        beanRegistrar(registry, JsfConsumerAnnotationBeanPostProcessor.class, null);
    }

    private void registerJsfBeanFactoryPostProcessor(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        final Object[] constructorArg = getConstructorArg(metadata);
        beanRegistrar(registry, JsfBeanFactoryPostProcessor.class, constructorArg);

    }

    private void beanRegistrar(BeanDefinitionRegistry registry, Class<?> clazz, Object[] constructorArg) {
        BeanDefinitionBuilder builder = rootBeanDefinition(clazz);

        if (constructorArg != null) {
            for (Object arg : constructorArg) {
                builder.addConstructorArgValue(arg);
            }
        }

        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), registry);
    }

    private Object[] getConstructorArg(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableJsf.class.getName()));

        String index = emptyToNull(attributes.getString("index"));

        String isConsumerOpenSecurity = emptyToNull(attributes.getString("isConsumerOpenSecurity"));

        String isProviderOpenSecurity = emptyToNull(attributes.getString("isProviderOpenSecurity"));
        String tokenFile = emptyToNull(attributes.getString("tokenFile"));
        String providerSecurityLevel = emptyToNull(attributes.getString("providerSecurityLevel"));

        return new Object[]{index, isConsumerOpenSecurity, isProviderOpenSecurity, tokenFile, providerSecurityLevel};
    }


    private static String emptyToNull(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        return source;
    }

}
