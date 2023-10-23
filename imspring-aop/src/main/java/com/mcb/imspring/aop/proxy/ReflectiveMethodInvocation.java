package com.mcb.imspring.aop.proxy;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public class ReflectiveMethodInvocation implements MethodInvocation {

    protected Object target;

    protected Object proxy;

    protected Method method;

    protected Object[] arguments;

    public ReflectiveMethodInvocation(Object target, Object proxy, Method method, Object[] arguments) {
        this.target = target;
        this.proxy = proxy;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object proceed() throws Throwable {
        return method.invoke(target, arguments);
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }

    public Object getTarget() {
        return target;
    }

    public Object getProxy() {
        return proxy;
    }
}
