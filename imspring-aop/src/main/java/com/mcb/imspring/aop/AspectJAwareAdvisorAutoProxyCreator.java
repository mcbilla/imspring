package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.aop.proxy.ProxyFactory;
import com.mcb.imspring.aop.support.AbstractAutoProxyCreator;
import com.mcb.imspring.core.exception.BeansException;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor.EMPTY_ADVICE;

public class AspectJAwareAdvisorAutoProxyCreator extends AbstractAutoProxyCreator {

    /**
     * 在容器加载的过程中获取所有切面和其对应的增强通知
     * key 是切面的 beanName，value 是该切面的所有通知增强器
     */
    private Map<String, List<AspectJExpressionPointcutAdvisor>> advisorsCache = new ConcurrentHashMap<>();

    /**
     * 前置处理，遍历所有的切面信息，然后将切面信息保存在缓存中
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isAspect(bean)) {
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            List<AspectJExpressionPointcutAdvisor> advisors = new ArrayList<>();
            for (Method method : declaredMethods) {
                advisors.add(new AspectJExpressionPointcutAdvisor(method, bean, beanName));
            }
            advisorsCache.put(beanName, advisors);
        }
        return bean;
    }

    /**
     * 后置处理
     * 1、获取切面方法：首先会从缓存中拿到所有的切面信息，和该 bean 的所有方法进行匹配，然后找到所有需要进行 AOP 的方法。
     * 2、创建 AOP 代理对象：结合需要进行 AOP 的方法，选择 Cglib 或 JDK，创建 AOP 代理对象。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (isAspect(bean)) {
            return bean;
        }
        // 遍历 advisorCache，逐一匹配
        for (List<AspectJExpressionPointcutAdvisor> advisors : advisorsCache.values()) {
            ProxyFactory proxyFactory = null;
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                // 使用 Pointcut 对象匹配当前 bean 对象
                if (this.isNotEmptyAdvice(advisor) && advisor.getPointcut().getClassFilter().matchers(bean.getClass())) {
                    if (proxyFactory == null) {
                        proxyFactory = new ProxyFactory();
                        proxyFactory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
                        TargetSource targetSource = new TargetSource(bean, advisor.getAspectJBean(), bean.getClass(), bean.getClass().getInterfaces());
                        proxyFactory.setTargetSource(targetSource);
                    }
                    proxyFactory.addAdvisors(advisor);
                }
            }
            if (proxyFactory != null) {
                return proxyFactory.getProxy();
            }
        }
        // 匹配失败，返回 bean
        return bean;
    }

    private boolean isAspect(Object bean) {
        return bean.getClass().isAnnotationPresent(Aspect.class);
    }

    private boolean isNotEmptyAdvice(AspectJExpressionPointcutAdvisor advisor) {
        return !advisor.getAdvice().equals(EMPTY_ADVICE);
    }
}
