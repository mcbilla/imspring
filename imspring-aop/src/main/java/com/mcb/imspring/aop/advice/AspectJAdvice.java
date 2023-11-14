package com.mcb.imspring.aop.advice;

import org.aopalliance.aop.Advice;

public interface AspectJAdvice extends Advice {
    String getAspectName();

    String getAspectMethodName();
}
