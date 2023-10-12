package com.mcb.imspring.core.context;

import java.util.List;

/**
 * Spring IOC核心容器
 */
public interface BeanFactory {
    /**
     * 是否存在指定name的Bean？
     */
    boolean containsBean(String name);

    /**
     * 根据name返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
     */
    <T> T getBean(String name);

    /**
     * 根据name返回唯一Bean，未找到抛出NoSuchBeanDefinitionException，找到但type不符抛出BeanNotOfRequiredTypeException
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * 根据type返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * 根据type返回一组Bean，未找到返回空List
     */
    <T> List<T> getBeans(Class<T> requiredType);
}
