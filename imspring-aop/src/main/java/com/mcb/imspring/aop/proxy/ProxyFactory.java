package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.AdvisedSupport;
import com.mcb.imspring.aop.proxy.AopProxy;
import com.mcb.imspring.aop.proxy.CglibAopProxy;
import com.mcb.imspring.aop.proxy.JdkDynamicAopProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyFactory extends AdvisedSupport {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AopProxy createAopProxy() {
        return new JdkDynamicAopProxy(this);
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }
}
