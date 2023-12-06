package com.mcb.imspring.tx;

import com.mcb.imspring.aop.utils.AopUtils;
import com.mcb.imspring.tx.advisor.TransactionAspectSupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * TransactionInterceptor 可以看作一种特殊的 aop advice，主要用于事务管理
 */
public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);
        return invokeWithinTransaction(methodInvocation.getMethod(), targetClass, new InvocationCallback() {
            @Override
            public Object proceedWithInvocation() throws Throwable {
                return methodInvocation.proceed();
            }
        });
    }
}