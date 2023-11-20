package com.mcb.imspring.aop;

import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.PointcutAdvisor;
import com.mcb.imspring.aop.advisor.TargetSource;
import com.mcb.imspring.aop.pointcut.Pointcut;
import com.mcb.imspring.aop.proxy.ProxyFactory;
import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.DefaultListableBeanFactory;
import com.mcb.imspring.core.common.OrderComparator;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.InstantiationAwareBeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.StringUtils;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AOP 的入口，实现 BeanPostProcessor 接口，主要做了两件事：
 * 1、前置处理，遍历所有的切面和对应的通知信息，然后将信息保存在缓存中。
 * 2、后置处理，从缓存中拿到所有的通知和当前 bean 的所有方法进行匹配，如果适配就创建代理对象。
 */
public abstract class AbstractAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * beanFactory 通过 Aware 接口注入
     */
    protected DefaultListableBeanFactory beanFactory;

    /**
     * key 是 beanName，value 代表了这个 Bean 是否需要被代理
     */
    private final Map<Object, Boolean> advisedBeans = new ConcurrentHashMap<>(256);

    private final Map<Object, Object> earlyProxyReferences = new ConcurrentHashMap<>(16);

    protected static final Advisor[] DO_NOT_PROXY = null;

    /**
     * 前置处理，遍历所有的切面和对应的通知信息，然后将信息保存在缓存中。这里的所有通知包括事务 + AspectJ 通知
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object cacheKey = getCacheKey(bean.getClass(), beanName);

        // 如果已经包含了这个 key，不需要在进行判断了，直接返回即可
        if (this.advisedBeans.containsKey(cacheKey)) {
            return bean;
        }

        // 判断是否 SpringAOP 中的基础设施类
        if (isInfrastructureClass(bean.getClass())) {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return bean;
        }

        // 查找所有的切面，并放到缓存，这里相当于 Spring 的 shouldSkip() 方法
        findCandidateAdvisors();

        return bean;
    }

    /**
     * 后置处理
     * 1、获取切面方法：首先会从缓存中拿到所有的通知，和当前 bean 的所有方法进行匹配，找到和该 bean 适配的通知。
     * 2、创建 AOP 代理对象：结合需要进行 AOP 的方法，选择 Cglib 或 JDK，创建 AOP 代理对象。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null) {
            Object cacheKey = getCacheKey(bean.getClass(), beanName);
            if (this.earlyProxyReferences.remove(cacheKey) != bean) {
                return wrapIfNecessary(bean, beanName, cacheKey);
            }
        }
        return bean;
    }

    /**
     * 判断 bean 是否需要代理，如果需要代理就返回代理后的 bean，如果不需要代理就返回原来的 bean
     */
    private Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
            return bean;
        }
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }

        Advisor[] advisors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName);
        if (advisors != DO_NOT_PROXY) {
            this.advisedBeans.put(cacheKey, Boolean.TRUE);
            return createProxy(bean, beanName, advisors);
        }
        this.advisedBeans.put(cacheKey, Boolean.FALSE);
        return bean;
    }

    protected Object getCacheKey(Class<?> aClass, String beanName) {
        if (StringUtils.hasLength(beanName)) {
            return beanName;
        }
        else {
            return aClass;
        }
    }

    /**
     * 判断是否 SpringAOP 中的基础设施类，例如 Advice、Pointcut、Advisor
     */
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
                Pointcut.class.isAssignableFrom(beanClass) ||
                Advisor.class.isAssignableFrom(beanClass);
        if (retVal) {
            logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
        }
        return retVal;
    }

    protected Advisor[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName) {
        // 返回所有能应用在指定的 Bean 上的通知
        List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
        if (advisors.isEmpty()) {
            return DO_NOT_PROXY;
        }
        return advisors.toArray(new Advisor[]{});
    }

    protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        // 获取到所有的通知
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        // 从获取到的通知中筛选出能应用到这个 Bean 上的通知
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        // 扩展通知
        extendAdvisors(eligibleAdvisors);
        // 如果通知不为空，还需要进行排序
        if (!eligibleAdvisors.isEmpty()) {
            eligibleAdvisors = sortAdvisors(eligibleAdvisors);
        }
        return eligibleAdvisors;
    }

    /**
     * 扫描 beanFactory 中的所有 advisor，默认只会扫描到事务的 TransactionAttributeSourceAdvisor
     * 子类 {@link AnnotationAwareAspectJAutoProxyCreator} 会覆盖该方法，扫描并创建 AspectJ 类型的的通知
     */
    protected List<Advisor> findCandidateAdvisors() {
        return beanFactory.getBeans(Advisor.class);
    }

    /**
     * 找出所有通知，挨个匹配某个 bean 的所有方法
     * 如果匹配成功说明这个通知可以用来增强这个 bean，添加到返回队列
     */
    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        List<Advisor> eligibleAdvisors = new ArrayList<>();
        candidateAdvisors.forEach(advisor -> {
            Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
            Method[] methods = beanClass.getDeclaredMethods();
            for (Method method : methods) {
                if (pointcut.getMethodMatcher().matches(method, beanClass)) {
                    eligibleAdvisors.add(advisor);
                    break;
                }
            }
        });
        return eligibleAdvisors;
    }

    /**
     * 通知根据 Ordered 来排序
     */
    private List<Advisor> sortAdvisors(List<Advisor> eligibleAdvisors) {
        eligibleAdvisors.sort(OrderComparator.INSTANCE);
        return eligibleAdvisors;
    }

    /**
     * 创建代理，ProxyFactory 里面自动判断使用 jdk 代理或者 cglib 代理
     */
    protected Object createProxy(Object bean, String beanName, Advisor[] advisors) {
        ProxyFactory proxyFactory = null;
        for (Advisor advisor : advisors) {
            if (proxyFactory == null) {
                proxyFactory = new ProxyFactory();
                proxyFactory.setTargetSource(getCustomTargetSource(bean, advisor));
            }
            proxyFactory.addAdvisors(advisor);
        }
        return proxyFactory.getProxy();
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        this.earlyProxyReferences.put(cacheKey, bean);
        return wrapIfNecessary(bean, beanName, cacheKey);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    protected abstract void extendAdvisors(List<Advisor> advisors);

    protected abstract TargetSource getCustomTargetSource(Object bean, Advisor advisor);
}
