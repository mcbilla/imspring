package com.mcb.imspring.aop.interceptor;

import com.mcb.imspring.aop.advice.AspectJMethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

    private final AspectJMethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(AspectJMethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        this.advice.before(methodInvocation.getMethod(), methodInvocation.getThis(), methodInvocation.getArguments());
        return methodInvocation.proceed();
    }
}
