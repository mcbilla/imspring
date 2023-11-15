package com.mcb.imspring.aop.advice;

import org.aopalliance.intercept.MethodInterceptor;

public interface AspectJAdvice extends MethodInterceptor {
    String getAspectName();

    String getAspectMethodName();
}
