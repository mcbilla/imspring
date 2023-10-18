package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.AdvisedSupport;

public class ProxyFactory extends AdvisedSupport implements AopProxy{

    public AopProxy createAopProxy() {
        return new JdkDynamicAopProxy(this);
    }


    @Override
    public Object getProxy() {
        return createAopProxy().getProxy();
    }
}
