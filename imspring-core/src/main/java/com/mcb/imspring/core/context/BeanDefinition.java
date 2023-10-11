package com.mcb.imspring.core.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BeanDefinition implements Comparable<BeanDefinition>{
    // 全局唯一的Bean Name:
    private final String name;

    // Bean的声明类型:
    private final Class<?> beanClass;

    // 构造器
    private final Constructor constructor;

    // Bean的实例:
    private Object instance = null;

    // 工厂方法名称
    private final String factoryName;

    // 工厂方法
    private final Method factoryMethod;

    public BeanDefinition(String name, Class<?> beanClass, Constructor constructor) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
        this.factoryName = null;
        this.factoryMethod = null;
    }

    public BeanDefinition(String name, Class<?> beanClass, Constructor constructor, String factoryName, Method factoryMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
        this.factoryName = factoryName;
        this.factoryMethod = factoryMethod;
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

    public String getFactoryName() {
        return factoryName;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    @Override
    public int compareTo(BeanDefinition o) {
        return 0;
    }
}
