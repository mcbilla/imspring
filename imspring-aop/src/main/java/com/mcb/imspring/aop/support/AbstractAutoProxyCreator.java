package com.mcb.imspring.aop.support;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.BeanPostProcessor;

public abstract class AbstractAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {
    /**
     * beanFactory 通过 Aware 接口注入
     */
    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
