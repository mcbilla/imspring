package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.core.common.Ordered;
import org.aopalliance.aop.Advice;

public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered {
    private Integer order;

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
        return Ordered.LOWEST_PRECEDENCE;
    }
}
