package com.mcb.imspring.aop.test.aspect;

import com.mcb.imspring.aop.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.core.annotation.Component;
import org.aspectj.lang.annotation.Aspect;

@Component
@Aspect
public class MyAspect extends AspectJExpressionPointcutAdvisor {

    public MyAspect() {
        this.setAdvice(new BeforeAdvice());
        this.setExpression("execution(* com.mcb.imspring.aop..*.*Service*.*(..))");
    }
}
