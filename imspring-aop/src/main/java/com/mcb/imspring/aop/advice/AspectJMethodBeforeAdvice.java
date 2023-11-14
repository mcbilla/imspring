package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice{

    public AspectJMethodBeforeAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectBean);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public void before(Method method, Object[] args, Object target) throws InvocationTargetException, IllegalAccessException {
        invokeAdviceMethod(getJoinPoint(), null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
