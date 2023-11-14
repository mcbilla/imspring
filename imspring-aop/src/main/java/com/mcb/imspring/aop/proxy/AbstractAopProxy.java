package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.pointcut.MethodMatcher;

import java.lang.reflect.Method;

public abstract class AbstractAopProxy implements AopProxy{

    protected final AdvisedSupport advised;

    public AbstractAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    /**
     * 对原始对象创建 jdk 或者 cglib 动态代理后，统一封装 ReflectiveMethodInvocation，内部通过反射调用
     * 这里为了方便并没有实现 CglibMethodInvocation 来处理 cglib 动态代理的对象
     */
    protected Object doInvoke(Object o, Method method, Object[] args) throws Throwable {
        // 将 bean 的原始 method 封装成 MethodInvocation 实现类对象
        ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(this.advised.getTargetSource().getTarget(),
                this.advised.getTargetSource().getProxy(), method, args, this.advised.getAdvisors());
        return invocation.proceed();
    }
}
