package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.aop.utils.AopUtils;
import com.mcb.imspring.core.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;
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
        return super.isInfrastructureClass(beanClass) || AopUtils.isAspect(beanClass);
    }

    /**
     * 调用父类找到所有候选的增强器（通知方法），并为 bean 工厂中的所有的 AspectJ 切面构建Advisor增强器
     */
    @Override
    protected List<Advisor> findCandidateAdvisors() {
        return this.buildAspectJAdvisors();
    }

    @Override
    protected TargetSource getCustomTargetSource(Object bean, Advisor advisor) {
        return new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
    }

    /**
     * 第一次调用创建通知并缓存起来
     * 后续调用，从缓存中取出所有通知返回
     */
    private List<Advisor> buildAspectJAdvisors() {
        // 第一次调用需要进行初始化
        if (this.aspectBeanNames == null) {
            List<Advisor> advisors = new ArrayList<>();
            this.aspectBeanNames = new ArrayList<>();
            String[] beanNames = ((ConfigurableListableBeanFactory) this.beanFactory).getBeanNamesForType(Object.class);
            for (String beanName : beanNames) {
                Object bean = this.beanFactory.getBean(beanName);
                // 如果 bean 是切面类型就进行解析
                if (AopUtils.isAspect(bean.getClass())) {
                    aspectBeanNames.add(beanName);
                    Method[] methods = bean.getClass().getDeclaredMethods();
                    List<Advisor> classAdvisors = new ArrayList<>();
                    for (Method method : methods) {
                        // 如果方法是通知类型就进行解析
                        if (AopUtils.isAdvice(method)) {
                            classAdvisors.add(new AspectJExpressionPointcutAdvisor(method, bean, beanName));
                        }
                    }
                    advisorsCache.put(beanName, classAdvisors);
                    advisors.addAll(classAdvisors);
                }
            }
            return advisors;
        }
        if (this.aspectBeanNames.isEmpty()) {
            return Collections.emptyList();
        }
        // 后续调用从缓存中取出通知返回
        List<Advisor> advisors = new ArrayList<>();
        for (String aspectName : this.aspectBeanNames) {
            List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
            if (cachedAdvisors != null) {
                advisors.addAll(cachedAdvisors);
            }
        }
        return advisors;
    }
}
