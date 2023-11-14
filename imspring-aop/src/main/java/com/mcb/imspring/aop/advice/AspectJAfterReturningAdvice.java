package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice{

    public AspectJAfterReturningAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectBean);
    }

    @Override
    public int getOrder() {
        return 4;
    }

    public void afterReturning(MethodInvocation methodInvocation, Object retVal) throws InvocationTargetException, IllegalAccessException {
        invokeAdviceMethod(getJoinPoint(), retVal, null);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object retVal = methodInvocation.proceed();
        this.afterReturning(methodInvocation, retVal);
        return retVal;
    }
}
