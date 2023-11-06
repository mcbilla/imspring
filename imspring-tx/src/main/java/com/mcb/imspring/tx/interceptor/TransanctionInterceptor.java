package com.mcb.imspring.tx.interceptor;

import com.mcb.imspring.aop.utils.AopUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TransanctionInterceptor extends TransactionAspectSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);

        return null;
    }
}