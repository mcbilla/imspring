package com.mcb.imspring.core.context;

import com.mcb.imspring.core.exception.BeansException;
import com.sun.istack.internal.Nullable;

public interface BeanPostProcessor {
    /**
     * Invoked after new Bean().
     */
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Invoked after bean.init() called.
     */
    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
