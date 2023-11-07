package com.mcb.imspring.core;

import com.mcb.imspring.core.exception.BeansException;

public interface ApplicationContext extends ConfigurableListableBeanFactory {
    BeanFactory getBeanFactory() throws IllegalStateException;

    /**
     * ApplicationContext 最核心的方法，完成所有初始化流程
     * @throws BeansException
     * @throws IllegalStateException
     */
    void refresh() throws BeansException, IllegalStateException;
}
