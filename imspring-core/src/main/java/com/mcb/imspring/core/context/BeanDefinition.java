package com.mcb.imspring.core.context;

import java.lang.reflect.Method;

public class BeanDefinition implements Comparable<BeanDefinition>{
    // 全局唯一的Bean Name:
    private final String name;

    // Bean的声明类型:
    private final Class<?> beanClass;

    // Bean的实例:
    private Object instance = null;

    // 工厂方法名称
    private final String factoryName;

    // 工厂方法
    private final Method factoryMethod;

    public BeanDefinition(String name, Class<?> beanClass, String factoryName, Method factoryMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.factoryName = factoryName;
        this.factoryMethod = factoryMethod;
    }

    @Override
    public int compareTo(BeanDefinition o) {
        return 0;
    }
}
