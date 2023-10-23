package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.aop.pointcut.MethodMatcher;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.CollectionUtils;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AdvisedSupport {
    private TargetSource targetSource;

    private MethodMatcher methodMatcher;

    private List<Advisor> advisors = new ArrayList<>();

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    public void addAdvisors(Advisor... advisors) {
        addAdvisors(Arrays.asList(advisors));
    }

    public void addAdvisors(Collection<Advisor> advisors) {
        if (!CollectionUtils.isEmpty(advisors)) {
            for (Advisor advisor : advisors) {
                Assert.notNull(advisor, "Advisor must not be null");
                this.advisors.add(advisor);
            }
        }
    }

    public List<Advisor> getAdvisors() {
        return advisors;
    }
}
