package com.mcb.imspring.aop.joinpoint;

import com.mcb.imspring.aop.proxy.ReflectiveMethodInvocation;
import com.mcb.imspring.core.utils.Assert;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

/**
 * join point 的概念：一个在程序执行期间的某一个操作，例如一个类、一个方法或者一个异常。
 * 目前支持方法连接点，所有的方法都是 join point，通过 point cut 筛选出需要增强的 join point。
 *
 * 比如原始方法 a()，需要先后调用 b()、c()、d()三个方法进行增强。
 * 在调用 b() 的时候，a() 是第一个 join point，这时候会产生一个代理对象 proxy1
 * 在调用 c() 的时候，对 proxy1 进行进行增强，proxy1.invoke() 是第二个 join point，这时候会产生一个代理对象 proxy2
 * 在调用 d() 的时候，对 proxy2 进行进行增强，proxy2.invoke() 是第三个 join point
 * 所以这里会产生三个 join point
 *
 */
public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint {
    private final ReflectiveMethodInvocation methodInvocation;

    private Object[] args;

    public MethodInvocationProceedingJoinPoint(ReflectiveMethodInvocation methodInvocation) {
        Assert.notNull(methodInvocation, "MethodInvocation must not be null");
        this.methodInvocation = methodInvocation;
    }

    @Override
    public void set$AroundClosure(AroundClosure aroundClosure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object proceed() throws Throwable {
        return this.methodInvocation.proceed();
    }

    @Override
    public Object proceed(Object[] objects) throws Throwable {
        Assert.notNull(objects, "Argument array passed to proceed cannot be null");
        if (objects.length != this.methodInvocation.getArguments().length) {
            throw new IllegalArgumentException("Expecting " +
                    this.methodInvocation.getArguments().length + " arguments to proceed, " +
                    "but was passed " + objects.length + " arguments");
        }
        this.methodInvocation.setArguments(objects);
        return this.methodInvocation.proceed();
    }

    @Override
    public Object getThis() {
        return this.methodInvocation.getProxy();
    }

    @Override
    public Object getTarget() {
        return this.methodInvocation.getTarget();
    }

    @Override
    public Object[] getArgs() {
        if (this.args == null) {
            this.args = this.methodInvocation.getArguments();
        }
        return this.args;
    }

    @Override
    public String toShortString() {
        return null;
    }

    @Override
    public String toLongString() {
        return null;
    }

    @Override
    public Signature getSignature() {
        return null;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public String getKind() {
        return null;
    }

    @Override
    public StaticPart getStaticPart() {
        return null;
    }
}
