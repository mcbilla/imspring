package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.core.annotation.Component;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Component
public class MyAspect {

    @Pointcut("execution(* com.mcb.imspring.aop.test.aspect.*Service.*(..))")
    public void getPointCut() {
    }

    @Before("getPointCut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("这是一个日志");
    }
}
