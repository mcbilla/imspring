package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.aop.proxy.ReflectiveMethodInvocation;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice{

    public AspectJMethodBeforeAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }

    public void before(Object object, Object[] args) throws InvocationTargetException, IllegalAccessException {
        invokeAdviceMethod(object, args);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        before(((ReflectiveMethodInvocation)methodInvocation).getProxy(), methodInvocation.getArguments());
        return methodInvocation.proceed();
    }
}
