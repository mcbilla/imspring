package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.matcher.MethodMatcher;
import org.aopalliance.intercept.MethodInterceptor;

public class AdvisedSupport {
    private TargetSource targetSource;

    private MethodInterceptor methodInterceptor;

    private MethodMatcher methodMatcher;
}
