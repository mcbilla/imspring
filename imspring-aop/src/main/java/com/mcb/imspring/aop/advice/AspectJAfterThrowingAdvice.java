package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;

import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice{
    public AspectJAfterThrowingAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        super(aspectJAdviceMethod, pointcut);
    }
}
