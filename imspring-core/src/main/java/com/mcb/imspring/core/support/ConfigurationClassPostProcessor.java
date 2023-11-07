package com.mcb.imspring.core.support;

import com.mcb.imspring.core.ConfigurableListableBeanFactory;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.context.BeanDefinitionRegistryPostProcessor;

/**
 * 处理@Configuration注解
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }
}
