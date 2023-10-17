package com.mcb.imspring.aop.matcher;

import java.lang.reflect.Method;

public class JdkRegexpMethodPointcut extends AbstractRegexpMethodPointcut{
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return false;
    }

    @Override
    public ClassFilter getClassFilter() {
        return null;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return null;
    }
}
