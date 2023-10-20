package com.mcb.imspring.aop.test.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class BeforeAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("这是一个BeforeAdvice");
        Object obj= methodInvocation.proceed();
        return obj;
    }
}
