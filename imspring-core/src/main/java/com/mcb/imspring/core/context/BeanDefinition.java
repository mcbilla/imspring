package com.mcb.imspring.core.context;

import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.BeanUtils;
import com.sun.istack.internal.Nullable;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * BeanDefinition 保存 Bean 的类信息、类实例、用户属性等，是 IOC 容器中的核心类
 * Spring 的 BeanDefinition 和实例是分开保存的（实例放在DefaultSingletonBeanRegistry中），这里为了简化放一起保存
 */
public class BeanDefinition implements Comparable<BeanDefinition>{
    // 全局唯一的 Bean Name
    private final String name;

    /**
     * Bean的实例，有两种实例化方式:
     * 1、如果是 @Bean 注入的 Bean，设置 factoryBeanName 和 factoryMethodName，使用 factoryMethod 创建实例
     * 2、如果是 @Component 注入的 Bean，设置 beanClass，使用默认构造器创建实例
     */
    private Object bean = null;

    // Bean的声明类型:
    private Class<?> beanClass;

    // 工厂bean名称
    @Nullable
    private String factoryBeanName;

    // 工厂方法名称
    @Nullable
    private String factoryMethodName;

    // @PostConstruct 方法名
    @Nullable
    private String initMethodName;

    // @PreDestroy 方法名
    @Nullable
    private String destroyMethodName;

    // 目标类型
    private Class<?> targetType;

    // bean的其他属性，比如排序、包含某些注解等
    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public BeanDefinition(String name) {
        this.name = name;
    }

    public BeanDefinition(String name, @Nullable Class<?> beanClass) {
        this.name = name;
        this.beanClass = beanClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }

    public void setAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        }
        else {
            removeAttribute(name);
        }
    }

    @Nullable
    public Object removeAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.remove(name);
    }

    @Nullable
    public Object getAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.get(name);
    }

    public boolean hasAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.containsKey(name);
    }

    @Override
    public int compareTo(BeanDefinition o) {
        return 0;
    }
}
