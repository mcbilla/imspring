package com.mcb.imspring.aop.proxy;

public class ProxyFactory extends AdvisedSupport {

    /**
     * 创建代理，实现了接口，默认使用jdk代理；没有实现接口，默认使用cglib代理
     */
    public AopProxy createAopProxy() {
        Class<?>[] ifcs = this.getTargetSource().getInterfaces();
        if (ifcs.length == 0) {
            return new CglibAopProxy(this);
        } else {
            return new JdkDynamicAopProxy(this);
        }
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }
}
