package com.mcb.imspring.aop.pointcut.impl;

import com.mcb.imspring.aop.pointcut.MethodMatcher;

import java.lang.reflect.Method;

public final class TrueMethodMatcher implements MethodMatcher {

    public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

    private TrueMethodMatcher() {
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

    @Override
    public String toString() {
        return "MethodMatcher.TRUE";
    }

    private Object readResolve() {
        return INSTANCE;
    }

}
