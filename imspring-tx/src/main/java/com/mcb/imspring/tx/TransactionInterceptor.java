package com.mcb.imspring.tx;

import com.mcb.imspring.aop.utils.AopUtils;
import com.mcb.imspring.tx.joinpoint.TransactionAspectSupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * TransactionInterceptor 可以看作一种特殊的 aop advice，主要用于事务管理
 */
public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("spring事务");
        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);
        return methodInvocation.proceed();
    }
}