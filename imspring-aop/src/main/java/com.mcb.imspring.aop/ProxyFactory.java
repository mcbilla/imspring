package com.mcb.imspring.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public <T> T createProxy(T bean, InvocationHandler handler) {
        Class<?> targetClass = bean.getClass();
        logger.debug("create proxy for bean {} @{}", targetClass.getName(), Integer.toHexString(bean.hashCode()));
        T proxy;
        // TODO 默认使用jdk代理
        proxy = (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), bean.getClass().getInterfaces(), handler);
        return proxy;
    }
}
