package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.core.annotation.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Component
@Aspect
public class MyAspect {

    @Pointcut("execution(* com.mcb.imspring.aop..*.*Service*.test(..))")
    public void getPointcut() {
    }

    @Around("getPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        System.out.println("我是一个AroundAdvice前置处理");
//        joinPoint.proceed();
        res = joinPoint.proceed(new String[]{"222"});
        System.out.println("我是一个AroundAdvice后置处理");
        return res;
    }

    @Before("getPointcut()")
    public void before() {
        System.out.println("我是一个BeforeAdvice");
    }

    @After("getPointcut()")
    public void after() {
        System.out.println("我是一个AfterAdvice");
    }

    @AfterReturning("getPointcut()")
    public void afterReturning() {
        System.out.println("我是一个AfterReturningAdvice");
    }

    @AfterThrowing("getPointcut()")
    public void afterThrowing() {
        System.out.println("我是一个AfterThrowingAdvice");
    }
}
