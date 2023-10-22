package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;

import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice{
    public AspectJAfterAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }
}
