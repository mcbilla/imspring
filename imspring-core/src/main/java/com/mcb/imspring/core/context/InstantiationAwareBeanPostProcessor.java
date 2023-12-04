package com.mcb.imspring.core.context;

import com.sun.istack.internal.Nullable;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    @Nullable
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    default boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
