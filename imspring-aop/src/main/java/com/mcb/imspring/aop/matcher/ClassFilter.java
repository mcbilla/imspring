package com.mcb.imspring.aop.matcher;

/**
 * 类级别的匹配
 */
public interface ClassFilter {
    boolean matchers(Class beanClass);
}
