package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.aop.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.core.annotation.Component;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.Pointcut;

@Component
public class MyAspect extends AspectJExpressionPointcutAdvisor {

    public MyAspect() {
        this.setAdvice(new BeforeAdvice());
        this.setExpression("execution(* com.mcb.imspring.aop.test.aspect.*Service.*(..))");
    }
}
