package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.aop.proxy.ReflectiveMethodInvocation;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice{

    public AspectJMethodBeforeAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public void before(MethodInvocation methodInvocation) throws InvocationTargetException, IllegalAccessException {
        JoinPoint jp = new MethodInvocationProceedingJoinPoint((ReflectiveMethodInvocation)methodInvocation);
        invokeAdviceMethod(jp, null, null);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        this.before(methodInvocation);
        return methodInvocation.proceed();
    }
}
