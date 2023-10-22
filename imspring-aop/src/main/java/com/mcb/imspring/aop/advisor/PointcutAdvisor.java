package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.pointcut.Pointcut;

public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}
