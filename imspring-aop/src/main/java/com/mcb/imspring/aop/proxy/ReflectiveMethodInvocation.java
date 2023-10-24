package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.Advisor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 封装需要增强的原始对象，并包含了advisor等增强的内容
 * Advisor的增强内容默认都通过反射来实现
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    protected Object target;

    protected Object proxy;

    protected Method method;

    protected Object[] arguments;

    protected final List<Advisor> advisors;

    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object target, Object proxy, Method method, Object[] arguments, List<Advisor> advisors) {
        this.target = target;
        this.proxy = proxy;
        this.method = method;
        this.arguments = arguments;
        this.advisors = advisors;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    /**
     * 理解proceed()的写法，是理解advisors链式调用的关键
     * @return
     * @throws Throwable
     */
    @Override
    public Object proceed() throws Throwable {
        // 2、直到所有advisor入栈完毕，然后advisor再进行出栈，调用advisor的真正方法
        if (this.currentInterceptorIndex == this.advisors.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }
        Advisor advisor = this.advisors.get(++this.currentInterceptorIndex);
        // 1、这里的invoke方法实际还是会调用到proceed()，advisor会不断入栈
        return ((MethodInterceptor) advisor.getAdvice()).invoke(this);
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
