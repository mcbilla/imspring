package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.pointcut.Pointcut;
import com.mcb.imspring.aop.pointcut.impl.TruePointcut;
import org.aopalliance.aop.Advice;

public class DefaultPointcutAdvisor extends AbstractPointcutAdvisor{
    private Pointcut pointcut = TruePointcut.INSTANCE;

    public DefaultPointcutAdvisor() {
    }

    public DefaultPointcutAdvisor(Advice advice) {
        this(TruePointcut.INSTANCE, advice);
    }

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        setAdvice(advice);
    }


    public void setPointcut(Pointcut pointcut) {
        this.pointcut = (pointcut != null ? pointcut : TruePointcut.INSTANCE);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }


    @Override
    public String toString() {
        return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice [" + getAdvice() + "]";
    }
}