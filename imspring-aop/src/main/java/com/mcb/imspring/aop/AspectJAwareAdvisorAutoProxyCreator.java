package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.aop.proxy.ProxyFactory;
import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor.EMPTY_ADVICE;

@Component
public class AspectJAwareAdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

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
                advisors.add(new AspectJExpressionPointcutAdvisor(method));
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
        /* 这里两个 if 判断很有必要，如果删除将会使程序进入死循环状态，
         * 最终导致 StackOverflowError 错误发生
         */
        if (bean instanceof AspectJExpressionPointcutAdvisor) {
            return bean;
        }
        if (bean instanceof MethodInterceptor) {
            return bean;
        }
        // 1.  遍历 advisorCache，逐一匹配
        for (List<AspectJExpressionPointcutAdvisor> advisors : advisorsCache.values()) {
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                // 2. 使用 Pointcut 对象匹配当前 bean 对象
                if (this.isNotEmptyAdvice(advisor) && advisor.getPointcut().getClassFilter().matchers(bean.getClass())) {
                    ProxyFactory proxyFactory = new ProxyFactory();
                    proxyFactory.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
                    proxyFactory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());

                    TargetSource targetSource = new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
                    proxyFactory.setTargetSource(targetSource);

                    // 3. 生成代理对象，并返回
                    return proxyFactory.getProxy();
                }
            }
        }
        // 匹配失败，返回 bean
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private boolean isAspect(Object bean) {
        return bean.getClass().isAnnotationPresent(Aspect.class);
    }

    private boolean isNotEmptyAdvice(AspectJExpressionPointcutAdvisor advisor) {
        return !advisor.getAdvice().equals(EMPTY_ADVICE);
    }
}
