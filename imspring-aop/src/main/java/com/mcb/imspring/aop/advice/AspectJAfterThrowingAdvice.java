package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice{
    public AspectJAfterThrowingAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectBean);
    }

    @Override
    public int getOrder() {
        return 3;
    }

    public void afterThrowing(Throwable ex, Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(null, ex);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        catch (Throwable ex) {
            this.afterThrowing(ex, mi.getMethod(), mi.getArguments(), mi.getThis());
            throw ex;
        }
    }
}
