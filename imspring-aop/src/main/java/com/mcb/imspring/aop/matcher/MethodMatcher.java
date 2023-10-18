package com.mcb.imspring.aop.matcher;

import java.lang.reflect.Method;

/**
 * 方法级别的匹配
 */
public interface MethodMatcher {

    boolean matchers(Method method, Class<?> targetClass);
}
