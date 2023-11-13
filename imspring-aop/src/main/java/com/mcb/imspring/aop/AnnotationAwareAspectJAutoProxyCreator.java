package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.aop.support.AbstractAutoProxyCreator;
import com.mcb.imspring.core.utils.ReflectionUtils;
import org.aspectj.lang.annotation.Aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationAwareAspectJAutoProxyCreator extends AbstractAutoProxyCreator {

    /**
     * 切面的 beanName 列表
     */
    private volatile List<String> aspectBeanNames;

    /**
     * key 是切面的 beanName，value 是对应的通知列表
     */
    private final Map<String, List<Advisor>> advisorsCache = new ConcurrentHashMap<>();

    /**
     * 子类增加判断是否 Aspect
     */
    @Override
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        return super.isInfrastructureClass(beanClass) || this.isAspect(beanClass);
    }

    private boolean isAspect(Class<?> beanClass) {
        return ReflectionUtils.hasAnnotation(beanClass, Aspect.class);
    }

    /**
     * 调用父类找到所有候选的增强器（通知方法），并为 bean 工厂中的所有的 AspectJ 切面构建Advisor增强器
     */
    @Override
    protected List<Advisor> findCandidateAdvisors() {
        List<Advisor> advisors = super.findCandidateAdvisors();
        advisors.addAll(this.buildAspectJAdvisors());
        return advisors;
    }

    @Override
    protected TargetSource getCustomTargetSource(Object bean, Advisor advisor) {
        return new TargetSource(bean, ((AspectJExpressionPointcutAdvisor) advisor).getAspectJBean(), bean.getClass(), bean.getClass().getInterfaces());
    }

    /**
     * 创建通知并缓存起来
     */
    private List<Advisor> buildAspectJAdvisors() {
        // 第一次扫描需要初始化
        if (this.aspectBeanNames == null) {

        }
        if (this.aspectBeanNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<Advisor> advisors = new ArrayList<>();

        return advisors;
    }
}
