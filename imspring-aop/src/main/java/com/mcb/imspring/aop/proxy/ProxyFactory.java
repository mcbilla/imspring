package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.AdvisedSupport;

public class ProxyFactory extends AdvisedSupport {

    public AopProxy createAopProxy() {
        return new JdkDynamicAopProxy(this);
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }
}
