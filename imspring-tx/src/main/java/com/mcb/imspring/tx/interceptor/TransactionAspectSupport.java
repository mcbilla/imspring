package com.mcb.imspring.tx.interceptor;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.tx.exception.TransactionException;
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

    @Override
    public void afterPropertiesSet() {

    }

    @Nullable
    protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
                                             final InvocationCallback invocation) {
        Object retVal = null;
        try {
            retVal = invocation.proceedWithInvocation();
        } catch (Throwable e) {
            throw new TransactionException(e);
        }
        return retVal;
    }

    @FunctionalInterface
    protected interface InvocationCallback {

        @Nullable
        Object proceedWithInvocation() throws Throwable;
    }
}
