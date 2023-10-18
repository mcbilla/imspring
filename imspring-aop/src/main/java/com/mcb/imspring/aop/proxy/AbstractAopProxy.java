package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.AdvisedSupport;

public abstract class AbstractAopProxy implements AopProxy {
    protected AdvisedSupport advised;

    public AbstractAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

}
