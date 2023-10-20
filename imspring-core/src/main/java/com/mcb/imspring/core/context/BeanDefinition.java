package com.mcb.imspring.core.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 保存bean的相关信息，spring中的instance和BeanDefinition是分开保存的（instance存在DefaultSingletonBeanRegistry中）
 * 这里为了简化，全部放一起保存
 */
public class BeanDefinition implements Comparable<BeanDefinition>{
    // 全局唯一的Bean Name:
    private final String name;

    // Bean的声明类型:
    private final Class<?> beanClass;

    // 构造器
    private final Constructor constructor;

    // Bean的实例:
    private Object instance = null;

    public BeanDefinition(String name, Class<?> beanClass, Constructor constructor) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    @Override
    public int compareTo(BeanDefinition o) {
        return 0;
    }
}
