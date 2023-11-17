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
 * 保存bean的相关信息，spring中的instance和BeanDefinition是分开保存的（instance存在DefaultSingletonBeanRegistry中）
 * 这里为了简化，全部放一起保存
 */
public class BeanDefinition implements Comparable<BeanDefinition>{
    // 全局唯一的Bean Name:
    private final String name;

    // Bean的声明类型:
    private final Class<?> beanClass;

    // Bean的实例:
    private Object bean = null;

    // 工厂bean名称
    @Nullable
    private String factoryBeanName;

    // 工厂方法名称
    @Nullable
    private String factoryMethodName;

    // 构造函数参数值
    @Nullable
    private Object[] constructorArgumentValues;

    @Nullable
    private String initMethodName;

    @Nullable
    private String destroyMethodName;

    // bean的其他属性，比如排序、包含某些注解等
    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public BeanDefinition(@Nullable Class<?> beanClass) {
        this(beanClass.getSimpleName(), beanClass, null);
    }

    public BeanDefinition(String name, @Nullable Class<?> beanClass) {
        this(name, beanClass, null);
    }

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
