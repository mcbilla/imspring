package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.proxy.ReflectiveMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

public class AspectJAroundAdvice extends AbstractAspectJAdvice{
    public AspectJAroundAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        JoinPoint jp = new MethodInvocationProceedingJoinPoint((ReflectiveMethodInvocation)methodInvocation);
        return invokeAdviceMethod(jp, null, null);
    }
}
