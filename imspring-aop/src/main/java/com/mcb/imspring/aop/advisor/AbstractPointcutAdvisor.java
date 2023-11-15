package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.core.common.Ordered;
import org.aopalliance.aop.Advice;

public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered {
    private Integer order;

    protected Advice advice = EMPTY_ADVICE;

    public void setAdvice(Advice advice) {
        this.advice = advice;
        if (advice instanceof Ordered) {
            this.setOrder(((Ordered) advice).getOrder());
        }
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        if (this.order != null) {
            return this.order;
        }
        Advice advice = getAdvice();
        if (advice instanceof Ordered) {
            return ((Ordered) advice).getOrder();
        }
        return Ordered.DEFAULT_PRECEDENCE;
    }
}
