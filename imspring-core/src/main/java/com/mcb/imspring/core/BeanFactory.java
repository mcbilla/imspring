package com.mcb.imspring.core;

import java.util.List;

/**
 * Spring IOC核心容器
 */
public interface BeanFactory {
    boolean containsBean(String name);

    <T> T getBean(String name);

    <T> T getBean(String name, Class<T> requiredType);

    <T> T getBean(Class<T> requiredType);

    <T> List<T> getBeans(Class<T> requiredType);

    Class<?> getType(String name);
}
