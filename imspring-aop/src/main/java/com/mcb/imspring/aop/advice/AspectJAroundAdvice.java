package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAroundAdvice extends AbstractAspectJAdvice{

    public AspectJAroundAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectJBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectJBean);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return invokeAdviceMethod(getJoinPoint(), null, null);
    }
}
