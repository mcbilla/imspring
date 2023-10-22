package com.mcb.imspring.aop.pointcut;

/**
 * 筛选要拦截的目标类和目标方法
 */
public interface Pointcut {

    /**
     * Return the ClassFilter for this pointcut.
     * @return the ClassFilter (never {@code null})
     */
    ClassFilter getClassFilter();

    /**
     * Return the MethodMatcher for this pointcut.
     * @return the MethodMatcher (never {@code null})
     */
    MethodMatcher getMethodMatcher();
}
