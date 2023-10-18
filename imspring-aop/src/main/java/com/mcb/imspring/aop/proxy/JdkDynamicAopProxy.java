package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.AdvisedSupport;
import com.mcb.imspring.aop.matcher.MethodMatcher;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicAopProxy extends AbstractAopProxy implements InvocationHandler {

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
        return Proxy.newProxyInstance(getClass().getClassLoader(), advised.getClass().getInterfaces(), this);
    }

    /**
     * InvocationHandler 接口中的 invoke 方法具体实现，封装了具体的代理逻辑
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodMatcher methodMatcher = advised.getMethodMatcher();

        // 使用方法匹配器 methodMatcher 测试 bean 中原始方法 method 是否符合匹配规则
        if (methodMatcher != null && methodMatcher.matchers(method, advised.getTargetSource().getTargetClass())) {

            // 获取 Advice。MethodInterceptor 的父接口继承了 Advice
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();

            // 将 bean 的原始 method 封装成 MethodInvocation 实现类对象，
            // 将生成的对象传给 Adivce 实现类对象，执行通知逻辑
            return methodInterceptor.invoke(
                    new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
        } else {
            // 当前 method 不符合匹配规则，直接调用 bean 中的原始 method
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }
}
