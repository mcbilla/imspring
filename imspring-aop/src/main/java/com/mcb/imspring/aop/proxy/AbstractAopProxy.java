package com.mcb.imspring.aop.proxy;

import com.mcb.imspring.aop.advice.AspectJAdvice;
import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.aop.advisor.PointcutAdvisor;
import com.mcb.imspring.aop.pointcut.MethodMatcher;
import com.mcb.imspring.aop.pointcut.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mcb.imspring.aop.advisor.Advisor.EMPTY_ADVICE;

public abstract class AbstractAopProxy implements AopProxy{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final AdvisedSupport advised;

    public AbstractAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    /**
     * Spring 把 cglib 代理的对象使用 CglibMethodInvocation 来处理，把 jdk 代理的对象使用 ReflectiveMethodInvocation 来处理
     * 这里为了方便，把 jdk 和 cglib 代理的对象，统一封装 ReflectiveMethodInvocation，内部通过反射调用
     */
    protected Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 因为 advised 里面保存的是适配该 bean 的所有消息增强器，不一定全部都适配当前方法
        // 所以这里需要经过一定的筛选，找出适配当前方法的消息增强器
        List<Advisor> eligibleAdivors = findEligibleAdivors(this.advised.getAdvisors(), method, proxy);
        if (!eligibleAdivors.isEmpty()) {
            String[] adviceInfos = new String[eligibleAdivors.size()];
            for (int i = 0; i < eligibleAdivors.size(); i++) {
                AspectJAdvice advice = (AspectJAdvice) eligibleAdivors.get(i).getAdvice();
                adviceInfos[i] = advice.getAspectName() + "-" + advice.getAspectMethodName();
            }
            Object target = this.advised.getTargetSource().getTarget();
            logger.debug("use aop proxy enhance bean，target: [{}]，proxy:[{}]，method: [{}]，advices: {}", target.getClass().getSimpleName(),
                    proxy.getClass().getSimpleName(), method.getName(), adviceInfos);
            // 将 bean 的原始 method 封装成 MethodInvocation 实现类对象
            ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(target, proxy, method, args, eligibleAdivors);
            return invocation.proceed();
        } else {
            // 没有合适的消息增强器，调用 bean 中的原始 method
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }

    protected List<Advisor> findEligibleAdivors(List<Advisor> advisors, Method method, Object o) {
        List<Advisor> eligibleAdivors = new ArrayList<>();
        for (Advisor advisor : advisors) {
            if (advisor.getAdvice().equals(EMPTY_ADVICE)) {
                continue;
            }

            if (advisor instanceof PointcutAdvisor) {
                Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
                if (pointcut.getMethodMatcher().matchers(method, o.getClass())) {
                    eligibleAdivors.add(advisor);
                }
            }
        }
        return eligibleAdivors;
    }
}
