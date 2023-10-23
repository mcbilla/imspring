package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.joinpoint.ReflectiveMethodInvocation;
import com.mcb.imspring.aop.pointcut.MethodMatcher;

import java.lang.reflect.Method;

public abstract class AbstractAopProxy implements AopProxy{

    protected final AdvisedSupport advised;

    public AbstractAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    protected Object doInvoke(Object o, Method method, Object[] args) throws Throwable {
        MethodMatcher methodMatcher = this.advised.getMethodMatcher();

        // 使用方法匹配器 methodMatcher 测试 bean 中原始方法 method 是否符合匹配规则
        if (methodMatcher != null && methodMatcher.matchers(method, this.advised.getTargetSource().getTargetClass())) {
            // 将 bean 的原始 method 封装成 MethodInvocation 实现类对象，
            ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(this.advised.getTargetSource().getTarget(), this.advised.getTargetSource().getProxy(), method, args, this.advised.getAdvisors());
            return invocation.proceed();

        } else {
            // 当前 method 不符合匹配规则，直接调用 bean 中的原始 method
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }
}
