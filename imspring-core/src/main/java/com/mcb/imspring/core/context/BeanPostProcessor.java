package com.mcb.imspring.core.context;

import com.mcb.imspring.core.exception.BeansException;
import com.sun.istack.internal.Nullable;

/**
 * BeanPostProcessor 在 Bean 实例化完成，属性注入完成后执行，用于对 Bean 的实例进行修改。
 * 例如对 Bean 进行增强、添加额外的功能等。默认是会对整个 Spring 容器中所有的 bean 进行处理
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
