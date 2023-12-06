package com.mcb.imspring.tx.transaction.tm;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.sun.istack.internal.Nullable;

public interface PlatformTransactionManager extends TransactionManager {
    /**
     * 根据事务定义信息从事务环境中返加一个已存在的事务，或者创建一个新的事务，并用TransactionStatus描述这个事务的状态。
     * @param definition
     * @return
     * @throws TransactionException
     */
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    /**
     * 提交事务。如果事务状态已经被标识为rollback-only,则该方法将执行一个回滚事务操作。
     * @param status
     * @throws TransactionException
     */
    void commit(TransactionStatus status) throws TransactionException;

    /**
     * 回滚事务
     * @param status
     * @throws TransactionException
     */
    void rollback(TransactionStatus status) throws TransactionException;
}
