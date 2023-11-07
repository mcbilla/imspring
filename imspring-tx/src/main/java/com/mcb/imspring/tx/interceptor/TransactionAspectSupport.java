package com.mcb.imspring.tx.interceptor;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.TransactionDefinition;
import com.mcb.imspring.tx.transaction.TransactionManager;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public abstract class TransactionAspectSupport implements BeanFactoryAware, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BeanFactory beanFactory;

    private TransactionManager transactionManager;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void afterPropertiesSet() {

    }

    /**
     * 事务执行的核心方法
     */
    @Nullable
    protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
                                             final InvocationCallback invocation) {
        // 1、获取该方法对应的事务属性
        TransactionDefinition td = getTransactionAttribute(method, targetClass);
        
        // 2、找到一个合适的事务管理器

        // 3、拿到目标方法唯一标识
        Object retVal = null;
        try {
            retVal = invocation.proceedWithInvocation();
        } catch (Throwable e) {
            throw new TransactionException(e);
        }
        return retVal;
    }

    private TransactionDefinition getTransactionAttribute(Method method, Class<?> targetClass) {
        return null;
    }

    @FunctionalInterface
    protected interface InvocationCallback {

        @Nullable
        Object proceedWithInvocation() throws Throwable;
    }
}
