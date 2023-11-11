package com.mcb.imspring.core.support;

import com.mcb.imspring.core.ConfigurableListableBeanFactory;
import com.mcb.imspring.core.annotation.*;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.context.BeanDefinitionRegistryPostProcessor;
import com.mcb.imspring.core.io.ClassPathBeanDefinitionScanner;
import com.mcb.imspring.core.io.ConfigurationClassBeanDefinitionReader;
import com.mcb.imspring.core.utils.BeanUtils;
import com.mcb.imspring.core.utils.Conventions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 处理配置类，Spring 中两种情况可以成为配置类
 * 1、带有@Configuration注解
 * 2、带有@Component，@ComponentScan，@Import，@ImportResource注解之一，或者某个方法带有 @Bean 注解
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BeanDefinitionRegistry registry;

    /**
     * 表示有Configuration注解
     */
    public static final String CONFIGURATION_CLASS_FULL = "full";

    /**
     * 带有@Component，@ComponentScan，@Import，@ImportResource注解之一，或者某个方法带有 @Bean 注解
     */
    public static final String CONFIGURATION_CLASS_LITE = "lite";

    /**
     * BeanDefinition 的配置类型 key，含有该 key 同时表明该 BeanDefinition 已经处理过
     */
    public static final String CONFIGURATION_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");

    private static final Set<Class<? extends Annotation>> candidateIndicators = new HashSet<>(8);

    static {
        candidateIndicators.add(Component.class);
        candidateIndicators.add(ComponentScan.class);
        candidateIndicators.add(Import.class);
    }
    private ConfigurationClassBeanDefinitionReader reader;

    /**
     * 预加载所有配置类为 BeanDefinition
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.reader = new ConfigurationClassBeanDefinitionReader(registry);
        processConfigBeanDefinitions(registry);
    }

    /**
     * 对配置类进行增强
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        enhanceConfigurationClasses(beanFactory);
    }

    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        // 筛选出配置类，包括 FULL + LITE 两类配置类
        Set<BeanDefinition> configCandidates = findConfigCandidates(registry);
        // 从配置类解析出需要加载为Bean的类
        Set<ConfigurationClass> configClasses = parse(configCandidates);
        // 把这些类加载为BeanDefinition
        this.reader.loadBeanDefinitions(configClasses);
    }

    /**
     * 从 registry 筛选出配置类
     */
    private Set<BeanDefinition> findConfigCandidates(BeanDefinitionRegistry registry) {
        Set<BeanDefinition> configCandidates = new HashSet<>();
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE) != null) {
                // 如果有该标记说明已经处理过了，就不用重复处理
                logger.debug("Bean definition has already been processed as a configuration class: " + beanDef.getName());
            } else if (checkConfigurationClassCandidate(beanDef, registry)) {
                configCandidates.add(beanDef);
            }
        }
        return configCandidates;
    }

    /**
     * 检测 CONFIGURATION_CLASS_FULL 或者 CONFIGURATION_CLASS_LITE 类型的配置类
     */
    private boolean checkConfigurationClassCandidate(BeanDefinition beanDef, BeanDefinitionRegistry registry) {
        Class<?> beanClass = beanDef.getBeanClass();
        if (beanClass.isAnnotationPresent(Configuration.class)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
            return true;
        }
        for (Class<? extends Annotation> indicator : candidateIndicators) {
            if (beanClass.isAnnotationPresent(indicator)) {
                beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
                return true;
            }
        }
        return false;
    }

    /**
     * 解析配置类的 BeanDefinition，按照配置内容加载剩余的 BeanDefinition
     */
    private Set<ConfigurationClass> parse(Set<BeanDefinition> configCandidates) {
        Set<ConfigurationClass> res = new HashSet<>();
        for (BeanDefinition beanDef : configCandidates) {
            ConfigurationClass configClass = new ConfigurationClass(beanDef.getName(), beanDef.getBeanClass());
            processConfigurationClass(beanDef, configClass);
            res.add(configClass);
        }
        return res;
    }

    /**
     * 目前暂时处理 @Configuration、@ComponentScan 这两种配置类
     */
    private void processConfigurationClass(BeanDefinition beanDef, ConfigurationClass configClass) {
        if (BeanUtils.hasAnnotation(configClass.getBeanClass(), Configuration.class)) {
            Set<Method> beanMethods = retrieveBeanMethodMetadata(configClass.getBeanClass());
            for (Method method : beanMethods) {
                configClass.addBeanMethod(method);
            }
        }

        // 这里处理 @ComponentScan，因为新扫描到的类有可能也包含 @Configuration，所以扫描完之后需要再处理一遍配置类
        if (BeanUtils.hasAnnotation(configClass.getBeanClass(), ComponentScan.class)) {
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry);
            String basePackages = configClass.getBeanClass().getAnnotation(ComponentScan.class).value();
            scanner.scan(basePackages);
            processConfigBeanDefinitions(this.registry);
        }
    }

    /**
     * 检测配置类中带@Bean注解的方法
     */
    private Set<Method> retrieveBeanMethodMetadata(Class<?> sourceClass) {
        Set<Method> beanMethods = new HashSet<>();
        for(Method method : sourceClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                beanMethods.add(method);
            }
        }
        return beanMethods;
    }

    private void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {

    }
}
