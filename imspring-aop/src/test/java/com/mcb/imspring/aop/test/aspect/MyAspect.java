package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.core.annotation.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Component
@Aspect
public class MyAspect {

    @Pointcut("execution(* com.mcb.imspring.aop..*.*Service*.*(..))")
    public void getPointcut() {
    }

    @Around("getPointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("我是一个AroundAdvice前置处理");
        joinPoint.proceed();
        System.out.println("我是一个AroundAdvice后置处理");
    }

    @AfterReturning("getPointcut()")
    public void afterReturning() {
        System.out.println("我是一个AfterReturningAdvice");
    }

    @After("getPointcut()")
    public void after() {
        System.out.println("我是一个AfterAdvice");
    }

    @Before("getPointcut()")
    public void before() {
        System.out.println("我是一个BeforeAdvice");
    }

}
