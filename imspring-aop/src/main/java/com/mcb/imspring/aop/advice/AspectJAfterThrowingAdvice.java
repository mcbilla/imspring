package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.joinpoint.ReflectiveMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice{
    public AspectJAfterThrowingAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }

    @Override
    public int getOrder() {
        return 3;
    }

    public void afterThrowing(MethodInvocation methodInvocation, Throwable ex) throws InvocationTargetException, IllegalAccessException {
        JoinPoint jp = new MethodInvocationProceedingJoinPoint((ReflectiveMethodInvocation)methodInvocation);
        invokeAdviceMethod(jp, null, ex);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        }
        catch (Throwable ex) {
            this.afterThrowing(methodInvocation, ex);
            throw ex;
        }
    }
}
