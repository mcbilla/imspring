package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.aop.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.core.annotation.Component;

@Component
public class MyAspect extends AspectJExpressionPointcutAdvisor {

    public MyAspect() {
        this.setAdvice(new BeforeAdvice());
        this.setExpression("execution(* com.mcb.imspring.aop..*.*Service*.*(..))");
    }
}
