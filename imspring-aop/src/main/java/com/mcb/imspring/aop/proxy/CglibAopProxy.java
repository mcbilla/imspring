package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.AdvisedSupport;

import java.lang.reflect.InvocationHandler;

public class CglibAopProxy extends AbstractAopProxy{

    public CglibAopProxy(AdvisedSupport advised) {
        super(advised);
    }

    @Override
    public Object getProxy() {
        return null;
    }
}
