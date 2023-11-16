package com.mcb.imspring.core.context;

import java.lang.reflect.Constructor;

/**
 * ConfigurationClassBeanDefinition 是配置类的 @Bean 方法创建的 BeanDefinition
 * 这类 BeanDefinition 在实例化的时候不能调用构造器，而是直接执行方法完成实例化
 */
public class ConfigurationClassBeanDefinition extends BeanDefinition{
    public ConfigurationClassBeanDefinition(Class<?> beanClass) {
        super(beanClass);
    }

    public ConfigurationClassBeanDefinition(String name, Class<?> beanClass) {
        super(name, beanClass);
    }

    public ConfigurationClassBeanDefinition(String name, Class<?> beanClass, Constructor constructor) {
        super(name, beanClass, constructor);
    }
}
