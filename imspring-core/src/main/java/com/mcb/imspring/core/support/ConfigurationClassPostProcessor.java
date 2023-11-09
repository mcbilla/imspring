package com.mcb.imspring.core.support;

import com.mcb.imspring.core.ConfigurableListableBeanFactory;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.context.BeanDefinitionRegistryPostProcessor;
import com.mcb.imspring.core.io.ConfigurationClassBeanDefinitionReader;
import com.mcb.imspring.core.utils.Conventions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 处理配置类，Spring 中两种情况可以成为配置类
 * 1、带有@Configuration注解
 * 2、带有@Component，@ComponentScan，@Import，@ImportResource注解之一，某个方法带有 @Bean 注解
 * 这里为了简化，只支持第一种情况
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * 表示有Configuration注解
     */
    public static final String CONFIGURATION_CLASS_FULL = "full";

    /**
     * 带有@Component，@ComponentScan，@Import，@ImportResource注解之一，某个方法带有 @Bean 注解
     */
    public static final String CONFIGURATION_CLASS_LITE = "lite";

    public static final String CONFIGURATION_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");

    private ConfigurationClassBeanDefinitionReader reader;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        this.reader = new ConfigurationClassBeanDefinitionReader(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 筛选出配置类
        List<BeanDefinition> configCandidates = new ArrayList<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            if (checkConfigurationClassCandidate(beanDef, beanFactory)) {
                configCandidates.add(beanDef);
            }
        }
        if (configCandidates.isEmpty()) {
            return;
        }
        // 加载配置类和带@Bean的方法
        Set<Class<?>> configClasses = configCandidates.stream().map(BeanDefinition::getBeanClass).collect(Collectors.toSet());
        this.reader.loadBeanDefinitions(configClasses);
    }

    /**
     * 目前只支持 CONFIGURATION_CLASS_FULL 类型的配置类
     */
    private boolean checkConfigurationClassCandidate(BeanDefinition beanDef, ConfigurableListableBeanFactory beanFactory) {
        Class<?> beanClass = beanDef.getBeanClass();
        if (beanClass.isAnnotationPresent(Configuration.class)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
            return true;
        }
        return false;
    }
}
