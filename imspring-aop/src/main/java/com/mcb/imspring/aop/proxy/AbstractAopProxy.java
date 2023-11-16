package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advice.AspectJAdvice;
import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.PointcutAdvisor;
import com.mcb.imspring.aop.joinpoint.ReflectiveMethodInvocation;
import com.mcb.imspring.aop.pointcut.Pointcut;
import com.mcb.imspring.aop.utils.AopUtils;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mcb.imspring.aop.advisor.Advisor.EMPTY_ADVICE;

public abstract class AbstractAopProxy implements AopProxy{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final AdvisedSupport advised;

    public AbstractAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    /**
     * Spring 把 cglib 代理的对象使用 CglibMethodInvocation 来处理，把 jdk 代理的对象使用 ReflectiveMethodInvocation 来处理
     * 这里为了方便，把 jdk 和 cglib 代理的对象，统一封装 ReflectiveMethodInvocation
     */
    protected Object doInvoke(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object target = this.advised.getTargetSource().getTarget();
        // 筛选适配当前方法的消息增强器
        List<Advisor> chain = findEligibleAdivors(this.advised.getAdvisors(), method, proxy);
        if (chain.isEmpty()) {
            // 没有适配的消息增强器，直接调用 target 的原始 method
            if (AopUtils.isCglibProxy(proxy)) {
                return methodProxy.invoke(target, args);
            } else {
                return method.invoke(target, args);
            }
        } else {
            String[] adviceInfos = new String[chain.size()];
            for (int i = 0; i < chain.size(); i++) {
                Advice advice = chain.get(i).getAdvice();
                if (advice instanceof AspectJAdvice) {
                    adviceInfos[i] = ((AspectJAdvice)advice).getAspectName() + "." + ((AspectJAdvice)advice).getAspectMethodName();
                } else {
                    adviceInfos[i] = chain.get(i).toString();
                }
            }
            logger.debug("use aop proxy enhance bean，target: [{}]，proxy:[{}]，method: [{}]，advices: {}", target.getClass().getSimpleName(),
                    proxy.getClass().getSimpleName(), method.getName(), adviceInfos);
            ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(target, proxy, method, args, chain);
            return invocation.proceed();
        }
    }

    /**
     * 因为 advised 里面保存的是适配该 bean 的所有消息增强器，不一定全部都适配当前方法
     * 所以这里需要经过一定的筛选，找出适配当前方法的消息增强器
     */
    protected List<Advisor> findEligibleAdivors(List<Advisor> advisors, Method method, Object o) {
        List<Advisor> eligibleAdivors = new ArrayList<>();
        for (Advisor advisor : advisors) {
            // 空消息，直接跳过
            if (advisor.getAdvice().equals(EMPTY_ADVICE)) {
                continue;
            }
            // PointcutAdvisor 类型，包括 Advice 和 事务
            if (advisor instanceof PointcutAdvisor) {
                Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
                if (pointcut.getMethodMatcher().matches(method, o.getClass())) {
                    eligibleAdivors.add(advisor);
                }
            }
        }
        return eligibleAdivors;
    }

    /**
     * 给代理对象添加实现接口 SpringProxy
     */
    protected Class<?>[] completeProxiedInterfaces(Class<?>[] interfaces) {
        List<Class<?>> proxiedInterfaces = new ArrayList<>(Arrays.asList(interfaces));
        boolean addSpringProxy = !AopUtils.isInterfaceProxied(SpringProxy.class, interfaces);
        if (addSpringProxy) {
            proxiedInterfaces.add(SpringProxy.class);
        }
        return proxiedInterfaces.toArray(new Class[]{});
    }
}
