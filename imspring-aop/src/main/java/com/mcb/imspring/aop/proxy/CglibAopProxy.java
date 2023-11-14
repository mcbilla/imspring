package com.mcb.imspring.aop.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Cglib 动态代理，实现cglib的MethodInterceptor接口，类似于JDK中的InvocationHandler接口
 */
public class CglibAopProxy extends AbstractAopProxy implements MethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CglibAopProxy(AdvisedSupport advised) {
        super(advised);
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(advised.getTargetSource().getTargetClass());
        enhancer.setCallback(this);
        Object proxy = enhancer.create();
        logger.debug("create cglib proxy target: [{}]，proxy: [{}]", advised.getTargetSource().getTarget().getClass(), proxy.getClass());
        return proxy;
    }

    /**
     * @param proxy 被调用的代理对象
     * @param method 拦截的方法
     * @param objects 数组表示参数列表，基本数据类型需要传入其包装类型，如int-->Integer
     * @param methodProxy 表示对方法的代理，invokeSuper方法表示对被代理对象方法的调用
     * @return 执行结果
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return doInvoke(proxy, method, objects);
    }
}
