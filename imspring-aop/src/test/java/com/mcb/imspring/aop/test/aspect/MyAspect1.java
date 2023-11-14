package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.core.annotation.Component;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Component
@Aspect
public class MyAspect1 {
    @Pointcut("execution(* com.mcb.imspring.aop..*.*Service*.test(..))")
    public void getPointcut() {
    }

    @Before("getPointcut()")
    public void before() {
        System.out.println("我是另外一个切面的BeforeAdvice");
    }
}
