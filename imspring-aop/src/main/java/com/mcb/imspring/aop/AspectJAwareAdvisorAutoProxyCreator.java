package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.proxy.ProxyFactory;
import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.context.BeanFactory;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AspectJAwareAdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    private Map<String, List<Advisor>> advisorsCache = new ConcurrentHashMap<>();

    /**
     * 前置处理，遍历所有的切面信息，然后将切面信息保存在缓存中
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 后置处理
     * 1、获取切面方法：首先会从缓存中拿到所有的切面信息，和该 bean 的所有方法进行匹配，然后找到所有需要进行 AOP 的方法。
     * 2、创建 AOP 代理对象：结合需要进行 AOP 的方法，选择 Cglib 或 JDK，创建 AOP 代理对象。
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 1.  从 BeanFactory 查找 AspectJExpressionPointcutAdvisor 类型的对象
        List<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeans(AspectJExpressionPointcutAdvisor.class);
        for(AspectJExpressionPointcutAdvisor advisor : advisors) {
            // 2. 使用 Pointcut 对象匹配当前 bean 对象
            if (advisor.getPointcut().getClassFilter().matchers(bean.getClass())) {
                ProxyFactory proxyFactory = new ProxyFactory();

            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private boolean isAspect(Object bean) {
        return bean.getClass().isAnnotationPresent(Aspect.class);
    }

    private boolean isPointcut(Method method) {
        return method.isAnnotationPresent(Pointcut.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        this.beanFactory = beanFactory;
    }
}
