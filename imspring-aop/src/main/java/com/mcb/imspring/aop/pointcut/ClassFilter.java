package com.mcb.imspring.aop.pointcut;

/**
 * 类级别的匹配
 */
public interface ClassFilter {
    boolean matches(Class<?> beanClass);
}
