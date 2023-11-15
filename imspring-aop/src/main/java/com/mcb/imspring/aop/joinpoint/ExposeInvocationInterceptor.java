package com.mcb.imspring.aop.joinpoint;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.DefaultPointcutAdvisor;
import com.mcb.imspring.core.common.NamedThreadLocal;
import com.mcb.imspring.core.common.Ordered;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * ExposeInvocationInterceptor 是拦截器链中第一个拦截器，它的作用是在进入拦截器执行逻辑的时候，将原始 MethodInvocation 方法调用对象暴露到 ThreadLocal 中，在拦截器链执行完之后再还原
 */
public class ExposeInvocationInterceptor implements MethodInterceptor, Ordered {

    public static final ExposeInvocationInterceptor INSTANCE = new ExposeInvocationInterceptor();

    public static final Advisor ADVISOR = new DefaultPointcutAdvisor(INSTANCE) {
        @Override
        public String toString() {
            return ExposeInvocationInterceptor.class.getName() +".ADVISOR";
        }
    };

    private static final ThreadLocal<MethodInvocation> invocation =
            new NamedThreadLocal<>("Current AOP method invocation");

    public static MethodInvocation currentInvocation() throws IllegalStateException {
        MethodInvocation mi = invocation.get();
        if (mi == null) {
            throw new IllegalStateException(
                    "No MethodInvocation found: Check that an AOP invocation is in progress and that the " +
                            "ExposeInvocationInterceptor is upfront in the interceptor chain. Specifically, note that " +
                            "advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor! " +
                            "In addition, ExposeInvocationInterceptor and ExposeInvocationInterceptor.currentInvocation() " +
                            "must be invoked from the same thread.");
        }
        return mi;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        MethodInvocation oldInvocation = invocation.get();
        invocation.set(mi);
        try {
            return mi.proceed();
        }
        finally {
            invocation.set(oldInvocation);
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE >> 1;
    }
}
