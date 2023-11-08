package com.mcb.imspring.core;

import com.mcb.imspring.core.context.BeanDefinition;

public interface ConfigurableListableBeanFactory extends BeanFactory{
    String[] getBeanNamesForType(Class<?> type);

    String[] getBeanDefinitionNames();

    BeanDefinition getBeanDefinition(String beanName);

}
