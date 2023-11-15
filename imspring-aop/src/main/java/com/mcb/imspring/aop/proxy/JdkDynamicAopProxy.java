package com.mcb.imspring.aop.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理，实现JDK的InvocationHandler接口，自定义代理逻辑
 */
final public class JdkDynamicAopProxy extends AbstractAopProxy implements InvocationHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        super(advised);
    }

    /**
     * 第一个参数：getClass().getClassLoader()，使用handler对象的classloader对象来加载我们的代理对象
     * 第二个参数：advised.getClass().getInterfaces()，这里为代理类提供的接口是真实对象实现的接口，这样代理对象就能像真实对象一样调用接口中的所有方法
     * 第三个参数：handler，我们将代理对象关联到上面的InvocationHandler对象上
     * @return
     */
    @Override
    public Object getProxy() {
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), advised.getTargetSource().getInterfaces(), this);
        logger.debug("create jdk proxy target: [{}]，proxy: [{}]", advised.getTargetSource().getTarget().getClass(), proxy.getClass());
        return proxy;
    }

    /**
     *
     * @param proxy 被调用的代理对象
     * @param method 拦截的方法
     * @param args 方法参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doInvoke(proxy, method, args, null);
    }
}
