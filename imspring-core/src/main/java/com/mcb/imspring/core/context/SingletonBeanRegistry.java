package com.mcb.imspring.core.context;

import com.sun.istack.internal.Nullable;

/**
 * bean 实例的管理容器
 */
public interface SingletonBeanRegistry {

    void registerSingleton(String beanName, Object singletonObject);

    @Nullable
    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();
}
