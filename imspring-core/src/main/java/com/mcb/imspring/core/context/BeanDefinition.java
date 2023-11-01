package com.mcb.imspring.core.context;

import com.sun.istack.internal.Nullable;

import javax.annotation.PostConstruct;
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
    private Object bean = null;

    @Nullable
    private String initMethodName;

    @Nullable
    private String destroyMethodName;

    public BeanDefinition(String name, Class<?> beanClass, Constructor constructor) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
        setInitAndDestroyMethodName();
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

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    private void setInitAndDestroyMethodName() {
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                this.initMethodName =  method.getName();
            }
            if (method.isAnnotationPresent(PostConstruct.class)) {
                this.destroyMethodName = method.getName();
            }
        }
    }

    @Override
    public int compareTo(BeanDefinition o) {
        return 0;
    }
}
