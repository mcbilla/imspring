package com.mcb.imspring.aop.matcher;

import java.lang.reflect.Method;

public interface MethodMatcher {
    /**
     * 静态匹配:可以满足大部分使用场景了，用于条件不严格的时候
     */
    boolean matches(Method method, Class<?> targetClass);
}
