package com.mcb.imspring.core.context;

import com.mcb.imspring.core.exception.BeansException;
import com.sun.istack.internal.Nullable;

/**
 * BeanPostProcessor默认是会对整个Spring容器中所有的bean进行处理
 */
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
