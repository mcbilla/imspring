package com.mcb.imspring.core;

import com.mcb.imspring.core.context.BeanDefinition;
import com.sun.istack.internal.Nullable;

public interface ConfigurableListableBeanFactory extends BeanFactory{
    @Nullable
    String[] getBeanNamesForType(Class<?> type);

    @Nullable
    String[] getBeanDefinitionNames();

    @Nullable
    BeanDefinition getBeanDefinition(String beanName);

}
