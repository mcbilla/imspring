package com.mcb.imspring.aop.advisor;

import org.aopalliance.aop.Advice;

public interface Advisor {
    Advice EMPTY_ADVICE = new Advice() {};

    Advice getAdvice();
}
