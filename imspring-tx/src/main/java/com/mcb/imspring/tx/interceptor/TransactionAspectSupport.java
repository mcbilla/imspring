package com.mcb.imspring.tx.interceptor;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.td.TransactionAttribute;
import com.mcb.imspring.tx.transaction.tm.CallbackPreferringPlatformTransactionManager;
import com.mcb.imspring.tx.transaction.tm.PlatformTransactionManager;
import com.mcb.imspring.tx.transaction.tm.TransactionManager;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

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
     * 事务执行的核心方法，主流程如下：
     * 1、获取事务属性
     * 2、获取事务管理器
     * 3、创建开启事务
     * 4、执行目标方法
     * 5、提交事务或者回滚事务
     */
    @Nullable
    protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
                                             final InvocationCallback invocation) throws Throwable {
        // 1、获取该方法对应的事务属性
        final TransactionAttribute txAttr = getTransactionAttribute(method, targetClass);
        
        // 2、找到一个合适的事务管理器
        final TransactionManager tm = determineTransantionManager(txAttr);
        PlatformTransactionManager ptm = asPlatformTransactionManager(tm);

        // 3、joinpoint标识，用于确定事务名称，值是全路径
        final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

        // 声明式事务处理逻辑
        if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
            // 5、创建事务
            TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

            Object retVal;
            try {
                // 6、通过回调执行目标方法
                retVal = invocation.proceedWithInvocation();
            } catch (Throwable ex) {
                // 7、异常回滚/提交
                completeTransactionAfterThrowing(txInfo, ex);
                throw new TransactionException(ex);
            } finally {
                // 8、清除 ThreadLocal 中保存的事务信息
                cleanupTransactionInfo(txInfo);
            }

            // 9、提交事务，此时目标方法已经执行完成
            commitTransactionAfterReturning(txInfo);
            return retVal;
        }
        // 编程式事务处理逻辑，逻辑和上类似
        else {
            Object result;
            final ThrowableHolder throwableHolder = new ThrowableHolder();

            try {
                result = ((CallbackPreferringPlatformTransactionManager) ptm).execute(txAttr, status -> {
                    TransactionInfo txInfo = prepareTransactionInfo(ptm, txAttr, joinpointIdentification, status);
                    try {
                        return invocation.proceedWithInvocation();
                    } catch (Throwable ex) {
                        if (txAttr.rollbackOn(ex)) {
                            throw new TransactionException(ex);
                        } else {
                            throwableHolder.throwable = ex;
                            return null;
                        }
                    } finally {
                        cleanupTransactionInfo(txInfo);
                    }
                });

            } catch (Throwable ex) {
                if (throwableHolder.throwable != null) {
                    logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
                }
                throw ex;
            }
            if (throwableHolder.throwable != null) {
                throw throwableHolder.throwable;
            }
            return result;
        }
    }

    private TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        return null;
    }

    private TransactionManager determineTransantionManager(TransactionAttribute txAttr) {
        return null;
    }

    private PlatformTransactionManager asPlatformTransactionManager(TransactionManager tm) {
        return null;
    }

    private String methodIdentification(Method method, Class<?> targetClass, TransactionAttribute txAttr) {
        return null;
    }

    private TransactionInfo createTransactionIfNecessary(PlatformTransactionManager ptm, TransactionAttribute txAttr, String joinpointIdentification) {
        return null;
    }

    private void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {

    }

    private void cleanupTransactionInfo(TransactionInfo txInfo) {
    }

    private void commitTransactionAfterReturning(TransactionInfo txInfo) {

    }

    private TransactionInfo prepareTransactionInfo(PlatformTransactionManager ptm, TransactionAttribute txAttr, String joinpointIdentification, TransactionStatus status) {
        return null;
    }

    protected static final class TransactionInfo {
        @Nullable
        private final PlatformTransactionManager transactionManager;

        @Nullable
        private final TransactionAttribute transactionAttribute;

        private final String joinpointIdentification;

        @Nullable
        private TransactionStatus transactionStatus;

        @Nullable
        private TransactionInfo oldTransactionInfo;

        public TransactionInfo(@Nullable PlatformTransactionManager transactionManager,
                               @Nullable TransactionAttribute transactionAttribute, String joinpointIdentification) {

            this.transactionManager = transactionManager;
            this.transactionAttribute = transactionAttribute;
            this.joinpointIdentification = joinpointIdentification;
        }
    }

    @FunctionalInterface
    protected interface InvocationCallback {

        @Nullable
        Object proceedWithInvocation() throws Throwable;
    }

    /**
     * 在 lambda 外面定义 Throwable，在 lambda 里面再进行赋值的话，报错 Variable used in lambda expression should be final or effectively final 问题
     * 这个类的作用是为了解决这个问题
     */
    private static class ThrowableHolder {

        @Nullable
        public Throwable throwable;
    }
}
