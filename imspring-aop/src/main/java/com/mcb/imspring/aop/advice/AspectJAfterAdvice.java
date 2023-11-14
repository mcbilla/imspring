package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.proxy.ReflectiveMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice{
    public AspectJAfterAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName) {
        super(aspectJAdviceMethod, pointcut, aspectName);
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public void after(MethodInvocation methodInvocation) throws InvocationTargetException, IllegalAccessException {
        JoinPoint jp = new MethodInvocationProceedingJoinPoint((ReflectiveMethodInvocation)methodInvocation);
        invokeAdviceMethod(jp, null, null);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } finally {
            this.after(methodInvocation);
        }
    }
}
