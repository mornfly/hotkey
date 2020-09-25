package com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.config;

import com.jd.jsf.gd.config.RegistryConfig;
import com.jd.jsf.gd.config.spring.ConsumerBean;
import com.jd.jsf.gd.config.spring.ProviderBean;
import com.jd.jsf.gd.config.spring.ServerBean;
import com.jd.jsf.gd.filter.AbstractFilter;
import com.jd.jsf.gd.filter.ExcludeFilter;
import com.jd.jsf.gd.util.ClassLoaderUtils;
import com.jd.jsf.gd.util.Constants;
import com.jd.jsf.gd.util.JSFContext;
import com.jd.platform.hotkey.dashboard.warn.dongdong.jsf.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * Date: 2016/11/4 15:27
 * Email: wangyulie@jd.com
 */
public class JsfBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware, PriorityOrdered, EnvironmentAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    static final String JSF_CONSUMER_INJECTION_METADATA_BEAN = "JSF_CONSUMER_INJECTION_METADATA_BEAN";

    private Environment environment;
    private ConfigurableListableBeanFactory beanFactory;

    private JsfConsumerInjectionMetadata jsfConsumerInjectionMetadata = new JsfConsumerInjectionMetadata();

    private final String registryIndex;
    private final String consumerOpenSecurity;
    private final String providerOpenSecurity;
    private final String tokenFile;
    private final String providerSecurityLevel;


    public JsfBeanFactoryPostProcessor() {
        this(null, null, null, null, null);
    }

    public JsfBeanFactoryPostProcessor(String registryIndex, String isConsumerOpenSecurity, String isProviderOpenSecurity, String tokenFile, String providerSecurityLevel) {
        this.registryIndex = registryIndex;
        this.consumerOpenSecurity = isConsumerOpenSecurity;
        this.providerOpenSecurity = isProviderOpenSecurity;
        this.tokenFile = tokenFile;
        this.providerSecurityLevel = providerSecurityLevel;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // not need impl
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        final String registryConfig = buildRegistryConfig(registry);

        // 注册配置单例
        beanFactory.registerSingleton(JSF_CONSUMER_INJECTION_METADATA_BEAN, jsfConsumerInjectionMetadata);

        initContextParam();

        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            String className = beanDefinition.getBeanClassName();
            if (className == null) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                parseConsumer(clazz, registry, registryConfig);
                parseProvider(clazz, beanDefinitionName, registry, registryConfig);
            } catch (ClassNotFoundException e) {
                logger.warn("ClassNotFoundException className:{}", className);
            }
        }
    }

    private void initContextParam() {
        if (consumerOpenSecurity == null && providerOpenSecurity == null) {
            // 未开启安全配置
            return;
        }
        if (tokenFile == null) {
            throw new IllegalArgumentException("tokenFile must not be null");
        }

        String aTokenFile = environment.resolveRequiredPlaceholders(tokenFile);

        if (providerOpenSecurity != null) {
            String aProviderOpenSecurity = environment.resolveRequiredPlaceholders(providerOpenSecurity);
            if (Boolean.valueOf(aProviderOpenSecurity)) {
                JSFContext.putGlobalVal(Constants.SECURITY_TOKEN_SERVER_FILE_URL, aTokenFile);
                if (providerSecurityLevel != null) {
                    String aProviderSecurityLevel = environment.resolveRequiredPlaceholders(providerSecurityLevel);
                    JSFContext.putGlobalVal(Constants.SECURITY_IS_OPEN_PROVIDER, Integer.valueOf(aProviderSecurityLevel).toString());
                }
            }
        }
        if (consumerOpenSecurity != null) {
            String aConsumerOpenSecurity = environment.resolveRequiredPlaceholders(consumerOpenSecurity);
            if (Boolean.valueOf(aConsumerOpenSecurity)) {
                JSFContext.putGlobalVal(Constants.SECURITY_TOKEN_CLIENT_FILE_URL, aTokenFile);
            }
        }
    }

    private String buildRegistryConfig(BeanDefinitionRegistry registry) {
        if (registryIndex == null) {
            return null;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RegistryConfig.class);
        builder.addPropertyValue("index", registryIndex);

        return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), registry);
    }

    private ManagedList<RuntimeBeanReference> buildManagedList(String beanName) {
        ManagedList<RuntimeBeanReference> list = new ManagedList<RuntimeBeanReference>();
        list.add(new RuntimeBeanReference(beanName));
        return list;
    }

    /**
     * Obtain a lazily resolving resource proxy for the given name and type,
     * delegating to {@link #getResource} on demand once a method call comes in.
     *
     * @param element            the descriptor for the annotated field/method
     * @param requestingBeanName the name of the requesting bean
     * @return the resource object (never {@code null})
     * @see #getResource
     * @see Lazy
     * @since 4.2
     */
    private Object buildLazyResourceProxy(final JsfConsumerInjectedElement element, final String requestingBeanName) {
        TargetSource ts = new TargetSource() {
            @Override
            public Class<?> getTargetClass() {
                return element.lookupType;
            }

            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public Object getTarget() {
                return getResource(element, requestingBeanName);
            }

            @Override
            public void releaseTarget(Object target) {
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        if (element.lookupType.isInterface()) {
            pf.addInterface(element.lookupType);
        }
        ClassLoader classLoader = (this.beanFactory != null ?
                this.beanFactory.getBeanClassLoader() : null);
        return pf.getProxy(classLoader);
    }

    /**
     * Obtain the resource object for the given name and type.
     *
     * @param element            the descriptor for the annotated field/method
     * @param requestingBeanName the name of the requesting bean
     * @return the resource object (never {@code null})
     * @throws BeansException if we failed to obtain the target resource
     */
    private Object getResource(JsfConsumerInjectedElement element, String requestingBeanName) throws BeansException {
        if (this.beanFactory == null) {
            throw new NoSuchBeanDefinitionException(element.lookupType,
                    "No resource factory configured - specify the 'resourceFactory' property");
        }
        return autowireResource(this.beanFactory, element, requestingBeanName);
    }

    /**
     * Obtain a resource object for the given name and type through autowiring
     * based on the given factory.
     *
     * @param factory            the factory to autowire against
     * @param element            the descriptor for the annotated field/method
     * @param requestingBeanName the name of the requesting bean
     * @return the resource object (never {@code null})
     * @throws BeansException if we failed to obtain the target resource
     */
    private Object autowireResource(BeanFactory factory, JsfConsumerInjectedElement element, String requestingBeanName)
            throws BeansException {

        String name = element.name;
        Object resource = factory.getBean(name, element.lookupType);
        Set<String> autowiredBeanNames = Collections.singleton(name);

        if (factory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) factory;
            for (String autowiredBeanName : autowiredBeanNames) {
                if (beanFactory.containsBean(autowiredBeanName)) {
                    beanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
                }
            }
        }

        return resource;
    }


    private void parseConsumer(Class<?> clazz, BeanDefinitionRegistry registry, String registryConfig) {
        List<InjectionMetadata.InjectedElement> list = findJsfConsumerAnnotation(clazz);
        if (list.isEmpty()) {
            return;
        }

        // 注册到配置中心去
        jsfConsumerInjectionMetadata.register(clazz, new InjectionMetadata(clazz, list));

        // 循环生成BeanDefinition
        for (InjectionMetadata.InjectedElement injectedElement : list) {
            JsfConsumerInjectedElement element = ((JsfConsumerInjectedElement) injectedElement);
            Class<?> consumerClazz = element.lookupType;
            JsfConsumer jsfConsumer = element.jsfConsumer;

            String consumerBeanName = NameUtils.checkConsumerBeanName(consumerClazz, jsfConsumer.alias());
            if (consumerBeanName != null) {
                element.name = consumerBeanName;
            } else {
                consumerBeanName = NameUtils.buildConsumerBeanName(consumerClazz, jsfConsumer.alias(), jsfConsumer.id());

                element.name = consumerBeanName;

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ConsumerBean.class);
                builder.addPropertyValue("interfaceId", consumerClazz.getName());
                builder.addPropertyValue("protocol", jsfConsumer.protocol());
                builder.addPropertyValue("alias", jsfConsumer.alias());
                builder.addPropertyValue("timeout", jsfConsumer.timeout());
                builder.addPropertyValue("serialization", jsfConsumer.serialization());
                builder.addPropertyValue("url", jsfConsumer.url().length() == 0 ? null : jsfConsumer.url());
                builder.addPropertyValue("retries", jsfConsumer.retries());
                builder.addPropertyValue("register", jsfConsumer.register());
                builder.addPropertyValue("subscribe", jsfConsumer.subscribe());

                if (registryConfig != null) {
                    builder.addPropertyValue("registry", buildManagedList(registryConfig));
                }

                if (jsfConsumer.filterRef().length > 0) {
                    builder.addPropertyValue("filter", buildFilterList(registry, jsfConsumer.filterRef()));
                }

                if (jsfConsumer.parameters().length > 0) {
                    ManagedMap<String, Object> parameters = new ManagedMap<String, Object>();
                    for (JsfParameter jsfParameter : jsfConsumer.parameters()) {
                        String key = jsfParameter.key();
                        if (jsfParameter.hide()) {
                            key = "." + key;
                        }
                        String value = jsfParameter.keyValue();
                        parameters.put(key, new TypedStringValue(value, String.class));
                    }
                    builder.addPropertyValue("parameters", parameters);
                }

                registry.registerBeanDefinition(consumerBeanName, builder.getBeanDefinition());
            }
        }
    }

    private ManagedList<Object> buildFilterList(BeanDefinitionRegistry registry, String[] filterRef) {
        ManagedList<Object> list = new ManagedList<Object>();
        for (String filter : filterRef) {
            if (filter.startsWith("-")) {
                list.add(new ExcludeFilter(filter));
            } else {
                final BeanDefinition fd = registry.getBeanDefinition(filter);
                if (!registry.containsBeanDefinition(filter)) {
                    throw new IllegalArgumentException(String.format("custom filter:%s not found in spring context", filter));
                }
                if (fd.isSingleton()) {
                    logger.warn("If custom filter:\"{}\" used by multiple provider/consumer," +
                            " you need to set attribute scope=\"property\"!", filter);
                }

                if (fd.getBeanClassName() != null) {
                    try {
                        if (!AbstractFilter.class.isAssignableFrom(ClassLoaderUtils.forName(fd.getBeanClassName()))) {
                            throw new IllegalArgumentException("Failed to set "
                                    + filter + ", cause by type of \"" + filter + "\" is " + fd.getBeanClassName()
                                    + ", not " + AbstractFilter.class.getName());
                        }
                    } catch (ClassNotFoundException e) {
                    }
                }
                list.add(new RuntimeBeanReference(filter));
            }
        }
        return list;
    }


    private List<InjectionMetadata.InjectedElement> findJsfConsumerAnnotation(Class<?> clazz) {
        List<InjectionMetadata.InjectedElement> list = new ArrayList<InjectionMetadata.InjectedElement>();
        // FIELD
        list.addAll(findJsfConsumerAnnotationFromField(clazz));
        // SET
        list.addAll(findJsfConsumerAnnotationFromSetMethod(clazz));
        return list;
    }

    private List<JsfConsumerInjectedElement> findJsfConsumerAnnotationFromSetMethod(final Class<?> clazz) {
        final List<JsfConsumerInjectedElement> list = new ArrayList<JsfConsumerInjectedElement>();

        ReflectionUtils.doWithLocalMethods(clazz, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (bridgedMethod.isAnnotationPresent(JsfConsumer.class)) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@JsfConsumer annotation is not supported on static methods");
                        }
                        Class<?>[] paramTypes = method.getParameterTypes();
                        if (paramTypes.length != 1) {
                            throw new IllegalStateException("@JsfConsumer annotation requires a single-arg method: " + method);
                        }
                        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        list.add(new JsfConsumerInjectedElement(method, bridgedMethod, pd));
                    }
                }
            }
        });
        return list;
    }

    private List<JsfConsumerInjectedElement> findJsfConsumerAnnotationFromField(Class<?> clazz) {
        final List<JsfConsumerInjectedElement> list = new ArrayList<JsfConsumerInjectedElement>();

        ReflectionUtils.doWithLocalFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(JsfConsumer.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@JsfConsumer annotation is not supported on static fields");
                    }
                    list.add(new JsfConsumerInjectedElement(field, field, null));
                }
            }
        });
        return list;
    }

    private ManagedList<RuntimeBeanReference> registerServer(Class<?> interfaceId, Server[] servers, BeanDefinitionRegistry registry) {
        if (servers.length == 0) {
            return null;
        }
        ManagedList<RuntimeBeanReference> list = new ManagedList<RuntimeBeanReference>();
        for (Server server : servers) {
            for (int i = 0; i < server.serverNum(); i++) {
                String serverName = NameUtils.buildServerBeanName(interfaceId);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServerBean.class);
                builder.addPropertyValue("protocol", server.protocol());
                registry.registerBeanDefinition(serverName, builder.getBeanDefinition());
                list.add(new RuntimeBeanReference(serverName));
            }
        }
        return list;
    }


    private void parseProvider(Class<?> implClazz, String beanDefinitionName, BeanDefinitionRegistry registry, String registryConfig) {
        List<JsfProvider> list = findProviderAnnotation(implClazz);
        if (list.isEmpty()) {
            return;
        }
        for (JsfProvider jsfProvider : list) {
            // 同一个 jsfProvider 里同一个interfaceClass 不同alias共享同一份 server
            // 不同 jsfProvider， 不同 interfaceClass 都是独立的 server
            Class<?>[] interfaceIds = findInterfaceId(implClazz, jsfProvider);
            for (Class<?> interfaceId : interfaceIds) {
                ManagedList<?> serverList = registerServer(interfaceId, jsfProvider.server(), registry);
                String[] aliasArr = jsfProvider.alias();
                for (String alias : aliasArr) {
                    String providerBeanName = NameUtils.buildProviderBeanName(interfaceId);
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProviderBean.class);
                    builder.addPropertyReference("ref", beanDefinitionName);
                    builder.addPropertyValue("interfaceId", interfaceId.getName());
                    builder.addPropertyValue("alias", alias);
                    builder.addPropertyValue("server", serverList);
                    builder.addPropertyValue("register", jsfProvider.register());
                    builder.addPropertyValue("dynamic", jsfProvider.dynamic());
                    builder.addPropertyValue("weight", jsfProvider.weight());

                    if (registryConfig != null) {
                        builder.addPropertyValue("registry", buildManagedList(registryConfig));
                    }

                    if (jsfProvider.filterRef().length > 0) {
                        builder.addPropertyValue("filter", buildFilterList(registry, jsfProvider.filterRef()));
                    }

                    registry.registerBeanDefinition(providerBeanName, builder.getBeanDefinition());
                }
            }
        }
    }


    private Class<?>[] findInterfaceId(Class<?> implClazz, JsfProvider jsfProvider) {
        if (jsfProvider.interfaceClass().length == 0) {
            Class<?>[] interfaces = implClazz.getInterfaces();
            if (interfaces == null || interfaces.length == 0) {
                throw new IllegalStateException("Failed to export remote service class " + implClazz.getName()
                        + ", cause: the service class unimplemented any interfaces.");
            } else if (interfaces.length == 1) {
                return interfaces;
            } else {
                throw new IllegalStateException("Failed to export remote service class " + implClazz.getName()
                        + ", cause: the service class implemented more than one interfaces. " +
                        "you must defined at @JsfProvider.interfaceClass");
            }
        } else {
            return jsfProvider.interfaceClass();
        }
    }


    private List<JsfProvider> findProviderAnnotation(Class<?> clazz) {
        List<JsfProvider> list = new ArrayList<JsfProvider>();
        JsfProviders jsfProviders = clazz.getAnnotation(JsfProviders.class);
        if (jsfProviders != null) {
            list.addAll(Arrays.asList(jsfProviders.value()));
        }
        JsfProvider annotation = clazz.getAnnotation(JsfProvider.class);
        if (annotation != null) {
            list.add(annotation);
        }
        return list;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "JsfConsumerAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private class JsfConsumerInjectedElement extends InjectionMetadata.InjectedElement {

        private final Class<?> lookupType;

        private final boolean lazyLookup;

        private final JsfConsumer jsfConsumer;

        private String name;

        JsfConsumerInjectedElement(Member member, AnnotatedElement ae, PropertyDescriptor pd) {
            super(member, pd);
            this.jsfConsumer = ae.getAnnotation(JsfConsumer.class);
            this.lookupType = getResourceType();
            Lazy lazy = ae.getAnnotation(Lazy.class);
            this.lazyLookup = (lazy != null && lazy.value());
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            return (this.lazyLookup ? buildLazyResourceProxy(this, requestingBeanName) :
                    getResource(this, requestingBeanName));
        }

    }

}
