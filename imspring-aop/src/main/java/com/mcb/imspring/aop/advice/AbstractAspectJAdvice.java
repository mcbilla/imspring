package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice, MethodInterceptor {

    private final Class<?> aspectJClass;

    private final String aspectJMethodName;

    private final Class<?>[] aspectJParameterTypes;

    protected final Method aspectJAdviceMethod;
    private final AspectJExpressionPointcut pointcut;

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        this.aspectJClass = aspectJAdviceMethod.getDeclaringClass();
        this.aspectJMethodName = aspectJAdviceMethod.getName();
        this.aspectJParameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
    }

    protected Object invokeAdviceMethod(Object object, Object[] args) throws InvocationTargetException, IllegalAccessException {
        this.aspectJAdviceMethod.setAccessible(true);
        return this.aspectJAdviceMethod.invoke(object, args);
    }
}
