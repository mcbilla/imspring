package com.mcb.imspring.tx.proxy;

import com.mcb.imspring.aop.AnnotationAwareAspectJAutoProxyCreator;
import com.mcb.imspring.aop.advisor.Advisor;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.tx.TransactionAttributeSourceAdvisor;

import java.util.ArrayList;
import java.util.List;

/**
 * TransactionAttributeSourceAdvisor 的优先级较高，应该会被优先初始化，如果 bean 实例为空，说明是依赖了其他的 bean 正在初始化，直接返回空列表，避免循环依赖导致栈溢出的问题
 */
public class TransactionAutoProxyCreator extends AnnotationAwareAspectJAutoProxyCreator {
    @Override
    protected List<Advisor> findCandidateAdvisors() {
        BeanDefinition bd = beanFactory.getBeanDefinition(TransactionAttributeSourceAdvisor.class);
        if (bd.getBean() == null) {
            return new ArrayList<>();
        } else {
            return super.findCandidateAdvisors();
        }
    }
}
