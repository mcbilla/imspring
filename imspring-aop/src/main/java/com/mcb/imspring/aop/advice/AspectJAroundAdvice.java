package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.joinpoint.ProxyMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.JoinPointMatch;

import java.lang.reflect.Method;

public class AspectJAroundAdvice extends AbstractAspectJAdvice{

    public AspectJAroundAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectJBean) {
        super(aspectJAdviceMethod, pointcut, aspectName, aspectJBean);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        ProceedingJoinPoint pjp = new MethodInvocationProceedingJoinPoint(pmi);
        JoinPointMatch jpm = getJoinPointMatch(pmi);
        return invokeAdviceMethod(pjp, jpm, null, null);
    }
}
