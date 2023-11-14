package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice{
    public AspectJAfterThrowingAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectBean);
    }

    @Override
    public int getOrder() {
        return 3;
    }

    public void afterThrowing(MethodInvocation methodInvocation, Throwable ex) throws InvocationTargetException, IllegalAccessException {
        invokeAdviceMethod(getJoinPoint(), null, ex);
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
