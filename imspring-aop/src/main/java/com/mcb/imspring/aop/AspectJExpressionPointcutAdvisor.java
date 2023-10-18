package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.PointcutAdvisor;
import com.mcb.imspring.aop.matcher.Pointcut;
import org.aopalliance.aop.Advice;

public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    protected AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    protected Advice advice;

    public void setExpression(String expression) {
        this.pointcut.setExpression(expression);
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
