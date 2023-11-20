package com.mcb.imspring.core.context;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
