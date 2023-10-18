package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.matcher.Pointcut;

public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}
