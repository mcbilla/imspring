package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.aop.Advice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice {

    private final Class<?> declaringClass;

    private final String methodName;

    private final Class<?>[] parameterTypes;

    protected final Method aspectJAdviceMethod;
    private final AspectJExpressionPointcut pointcut;

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
    }

    protected Object invokeAdviceMethod(Method method, Object object, Object[] args) throws InvocationTargetException, IllegalAccessException {
        this.aspectJAdviceMethod.setAccessible(true);
        return this.aspectJAdviceMethod.invoke(object, args);
    }
}
