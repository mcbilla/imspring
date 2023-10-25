package com.mcb.imspring.core.context;

import com.mcb.imspring.core.exception.BeansException;
import com.sun.istack.internal.Nullable;

import java.util.List;

public interface BeanDefinitionRegistry {
    boolean containsBeanDefinition(String beanName);

    /**
     * 根据Name查找BeanDefinition，如果Name不存在，返回null
     */
    @Nullable
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    @Nullable
    BeanDefinition getBeanDefinition(Class<?> type);

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    BeanDefinition getBeanDefinition(String name, Class<?> requiredType);

    /**
     * 根据Type查找若干个BeanDefinition，返回0个或多个。
     */
    @Nullable
    List<BeanDefinition> getBeanDefinitions(Class<?> type);
}
