package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 封装需要增强的对象、匹配器、增强器等信息，ProxyFactory需要用到这些信息
 */
public class AdvisedSupport {
    private TargetSource targetSource;

    private final List<Advisor> advisors = new ArrayList<>();

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
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
