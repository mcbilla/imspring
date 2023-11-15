package com.mcb.imspring.aop.joinpoint;

import com.mcb.imspring.aop.advisor.Advisor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Joinpoint 表示对某个方法（构造方法或成员方法）或属性的调用。
 * 例如调用了 user.save() 方法，这个调用动作就属于一个 Joinpoint。Joinpoint 是一个动态的概念，Field、Method、Constructor 等对象是它的静态部分。
 * Joinpoint 是 Advice 操作的对象，Advice 对 Joinpoint 执行某些操作。
 * 再例如，当我们对用户进行新增操作前，需要进行权限校验。其中，调用 user.save() 的动作就是一个的 Joinpoint，权限校验就是一个 Advice，即对 Joinpoint（新增用户的动作）做 Advice（权限校验）。
 * 这里的 Joinpoint 对象实现 Joinpoint 的子接口--MethodInvocation，内部持有一条 Advice chain，进行链式调用
 */
public class ReflectiveMethodInvocation implements ProxyMethodInvocation {

    // 原始目标对象
    protected final Object target;

    // 代理目标对象
    protected final Object proxy;

    // 原始目标方法
    protected final Method method;

    // 原始目标参数
    protected Object[] arguments;

    // 用户自定义属性
    private Map<String, Object> userAttributes;

    // Advice chain
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

    @Override
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    /**
     * advisors 链式调用
     */
    @Override
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.advisors.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }
        Advisor advisor = this.advisors.get(++this.currentInterceptorIndex);
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

    @Override
    public Object getProxy() {
        return proxy;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        }
        else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
