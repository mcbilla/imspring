package com.mcb.imspring.aop.pointcut.impl;

import com.mcb.imspring.aop.pointcut.ClassFilter;
import com.mcb.imspring.aop.pointcut.MethodMatcher;
import com.mcb.imspring.aop.pointcut.Pointcut;

public final class TruePointcut implements Pointcut {

    public static final TruePointcut INSTANCE = new TruePointcut();

    /**
     * Enforce Singleton pattern.
     */
    private TruePointcut() {
    }

    @Override
    public ClassFilter getClassFilter() {
        return TrueClassFilter.INSTANCE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return TrueMethodMatcher.INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Pointcut.TRUE";
    }

}
