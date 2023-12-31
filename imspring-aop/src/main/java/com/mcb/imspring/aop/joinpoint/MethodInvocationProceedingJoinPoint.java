package com.mcb.imspring.aop.joinpoint;

import com.mcb.imspring.core.utils.Assert;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;


/**
 * ProceedingJoinPoint 是一种特殊的 JoinPoint，专门用来处理 Around Advice
 */
public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint {

    // 内部持有 JoinPoint
    private final ProxyMethodInvocation methodInvocation;

    // JoinPoint 参数
    private Object[] args;

    public MethodInvocationProceedingJoinPoint(ProxyMethodInvocation methodInvocation) {
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
        int actualLen = objects.length;
        int expectLen = this.methodInvocation.getArguments() != null ? this.methodInvocation.getArguments().length : 0;
        if (actualLen != expectLen) {
            throw new IllegalArgumentException("Expecting " +
                    expectLen + " arguments to proceed, " +
                    "but was passed " + actualLen + " arguments");
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
        return this.methodInvocation.getThis();
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
