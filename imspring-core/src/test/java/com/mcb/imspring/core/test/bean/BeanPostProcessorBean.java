package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;

@Component
public class BeanPostProcessorBean implements BeanPostProcessor {
    private String name;

    private Integer age;

    public BeanPostProcessorBean(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        BeanPostProcessorBean resultBean = new BeanPostProcessorBean("mcb", 123);
        return resultBean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BeanPostProcessorBean{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}
