package com.mcb.imspring.core.context;

import com.sun.istack.internal.Nullable;

import java.util.List;

public interface ConfigurableBeanFactory extends BeanFactory{
    List<BeanDefinition> findBeanDefinitions(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(String name);

    @Nullable
    BeanDefinition findBeanDefinition(String name, Class<?> requiredType);
}
