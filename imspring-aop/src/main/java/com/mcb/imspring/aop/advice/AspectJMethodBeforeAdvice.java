package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice{

    public AspectJMethodBeforeAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }

    public void before(Method method, Object object, Object[] args) throws InvocationTargetException, IllegalAccessException {
        invokeAdviceMethod(method, object, args);
    }

}
