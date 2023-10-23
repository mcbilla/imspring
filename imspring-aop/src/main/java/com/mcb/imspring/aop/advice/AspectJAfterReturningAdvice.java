package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.joinpoint.ReflectiveMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice{
    public AspectJAfterReturningAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }

    @Override
    public int getOrder() {
        return 4;
    }

    public void afterReturning(MethodInvocation methodInvocation, Object retVal) throws InvocationTargetException, IllegalAccessException {
        JoinPoint jp = new MethodInvocationProceedingJoinPoint((ReflectiveMethodInvocation)methodInvocation);
        invokeAdviceMethod(jp, retVal, null);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object retVal = methodInvocation.proceed();
        this.afterReturning(methodInvocation, retVal);
        return retVal;
    }
}
