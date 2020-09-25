package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;

import java.beans.PropertyDescriptor;

/**
 * Date: 2016/11/10 10:30
 * Email: wangyulie@jd.com
 */
public class JsfConsumerAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements
        MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findJsfConsumerMetadata(bean.getClass());
        if (metadata != null) {
            try {
                metadata.inject(bean, beanName, pvs);
            } catch (Throwable ex) {
                throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
            }
        }
        return pvs;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findJsfConsumerMetadata(beanType);
        if (metadata != null) {
            metadata.checkConfigMembers(beanDefinition);
        }
    }

    private InjectionMetadata findJsfConsumerMetadata(Class<?> beanType) {
        JsfConsumerInjectionMetadata injectionMetadata = ((JsfConsumerInjectionMetadata) beanFactory.getSingleton(JsfBeanFactoryPostProcessor.JSF_CONSUMER_INJECTION_METADATA_BEAN));
        return injectionMetadata.findMetadata(beanType);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "JsfConsumerAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
