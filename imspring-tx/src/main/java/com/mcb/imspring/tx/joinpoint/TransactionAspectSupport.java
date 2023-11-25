package com.mcb.imspring.tx.joinpoint;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.common.NamedThreadLocal;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.sync.TransactionSynchronizationManager;
import com.mcb.imspring.tx.transaction.td.DefaultTransactionAttribute;
import com.mcb.imspring.tx.transaction.td.DefaultTransactionDefinition;
import com.mcb.imspring.tx.transaction.td.TransactionAttribute;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;
import com.mcb.imspring.tx.transaction.tm.CallbackPreferringPlatformTransactionManager;
import com.mcb.imspring.tx.transaction.tm.PlatformTransactionManager;
import com.mcb.imspring.tx.transaction.tm.TransactionManager;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public abstract class TransactionAspectSupport implements BeanFactoryAware, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BeanFactory beanFactory;

    private TransactionManager transactionManager;

    private TransactionAttributeSource transactionAttributeSource;

    private static final ThreadLocal<TransactionInfo> transactionInfoHolder =
            new NamedThreadLocal<>("Current aspect-driven transaction");

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
                                             final InvocationCallback invocation) throws Throwable {

        // 1、获取事务属性源
        TransactionAttributeSource tas = getTransactionAttributeSource();
        
        // 2、根据属性源获取事务属性
        final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
        
        // 3、找到一个合适的事务管理器
        final TransactionManager tm = determineTransantionManager(txAttr);
        PlatformTransactionManager ptm = asPlatformTransactionManager(tm);

        // 4、获取事务方法的唯一标识
        final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

        // 声明式事务处理逻辑
        if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
            // 5、创建事务
            TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

            Object retVal;
            try {
                // 6、通过回调执行目标方法
                retVal = invocation.proceedWithInvocation();

                // 7、提交事务
                commitTransactionAfterReturning(txInfo);
            } catch (Throwable ex) {
                // 8、异常回滚/提交
                completeTransactionAfterThrowing(txInfo, ex);
                throw ex;
            } finally {
                // 9、清除当前事务状态
                cleanupTransactionInfo(txInfo);
            }

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

    /**
     * 获取事务管理器，默认DataSourceTransactionManager
     */
    protected TransactionManager determineTransantionManager(TransactionAttribute txAttr) {
        if (txAttr == null || this.beanFactory == null) {
            return getTransactionManager();
        }
        TransactionManager defaultTransactionManager = getTransactionManager();
        if (defaultTransactionManager == null) {
            defaultTransactionManager = this.beanFactory.getBean(TransactionManager.class);
        }
        return defaultTransactionManager;
    }

    private PlatformTransactionManager asPlatformTransactionManager(@Nullable Object transactionManager) {
        if (transactionManager == null || transactionManager instanceof PlatformTransactionManager) {
            return (PlatformTransactionManager) transactionManager;
        }
        else {
            throw new IllegalStateException(
                    "Specified transaction manager is not a PlatformTransactionManager: " + transactionManager);
        }
    }

    private String methodIdentification(Method method, Class<?> targetClass, TransactionAttribute txAttr) {
        return ((DefaultTransactionAttribute) txAttr).getDescriptor();
    }

    private TransactionInfo createTransactionIfNecessary(PlatformTransactionManager tm, TransactionAttribute txAttr, String joinpointIdentification) {
        TransactionStatus status = null;
        if (txAttr != null) {
            if (txAttr.getName() == null && txAttr instanceof DefaultTransactionDefinition) {
                ((DefaultTransactionDefinition) txAttr).setName(joinpointIdentification);
            }
            if (tm != null) {
                status = tm.getTransaction(txAttr);
            }
        } else {
            logger.debug("Skipping transactional joinpoint [" + joinpointIdentification +
                    "] because no transaction attribute or no transaction manager has been configured");
        }
        return prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
    }

    private void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
        if (txInfo != null && txInfo.getTransactionStatus() != null) {
            logger.debug("Completing transaction for [" + txInfo.getJoinpointIdentification() +
                    "] after exception: " + ex);

            if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
                try {
                    logger.debug("Rollback completing transaction for [" + txInfo.getJoinpointIdentification() +
                            "] after exception: " + ex);
                    txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
                } catch (RuntimeException | Error ex2) {
                    logger.error("Application exception overridden by rollback exception", ex);
                    throw ex2;
                }
            } else {
                try {
                    // 异常回滚规则不匹配，这种情况不做回滚
                    logger.debug("skip Rollback completing transaction for [" + txInfo.getJoinpointIdentification() +
                            "] after exception: " + ex);
                    txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
                } catch (RuntimeException | Error ex2) {
                    logger.error("Application exception overridden by commit exception", ex);
                    throw ex2;
                }
            }
        }
    }

    protected void cleanupTransactionInfo(TransactionInfo txInfo) {
        if (txInfo != null) {
            txInfo.restoreThreadLocalStatus();
        }
    }

    protected void commitTransactionAfterReturning(TransactionInfo txInfo) {
        if (txInfo != null && txInfo.getTransactionStatus() != null) {
            logger.debug("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]");
            txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
        }
    }

    protected TransactionInfo prepareTransactionInfo(PlatformTransactionManager tm, TransactionAttribute txAttr, String joinpointIdentification, TransactionStatus status) {
        TransactionInfo txInfo = new TransactionInfo(tm, txAttr, joinpointIdentification);

        if (txAttr != null) {
            logger.debug("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]");
            txInfo.newTransactionStatus(status);
        } else {
            logger.debug("No need to create transaction for [" + joinpointIdentification +
                    "]: This method is not transactional.");
        }
        txInfo.bindToThread();
        return txInfo;
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

        public void newTransactionStatus(@Nullable TransactionStatus status) {
            this.transactionStatus = status;
        }

        private void bindToThread() {
            // Expose current TransactionStatus, preserving any existing TransactionStatus
            // for restoration after this transaction is complete.
            this.oldTransactionInfo = transactionInfoHolder.get();
            transactionInfoHolder.set(this);
        }

        private void restoreThreadLocalStatus() {
            // Use stack to restore old transaction TransactionInfo.
            // Will be null if none was set.
            transactionInfoHolder.set(this.oldTransactionInfo);
        }

        public PlatformTransactionManager getTransactionManager() {
            return transactionManager;
        }

        public TransactionStatus getTransactionStatus() {
            return transactionStatus;
        }

        public String getJoinpointIdentification() {
            return this.joinpointIdentification;
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

    public void setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
        this.transactionAttributeSource = transactionAttributeSource;
    }

    public TransactionAttributeSource getTransactionAttributeSource() {
        return this.transactionAttributeSource;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
