package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;

@Component
public class BeanPostProcessorBean implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName + "你好，我是一个BeanPostProcessorBean前置处理");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName + "你好，我是一个BeanPostProcessorBean后置处理");
        return bean;
    }
}
