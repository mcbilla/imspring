package com.mcb.imspring.aop.pointcut;

import java.lang.reflect.Method;

/**
 * 方法级别的匹配
 */
public interface MethodMatcher {

    boolean matchers(Method method, Class<?> targetClass);
}
