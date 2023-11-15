package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice{

    public AspectJAfterAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectBean);
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public void after(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } finally {
            this.after(mi.getMethod(), mi.getArguments(), mi.getThis());
        }
    }
}
