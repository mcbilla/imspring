package com.mcb.imspring.tx.transaction.tm;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.sun.istack.internal.Nullable;

public interface PlatformTransactionManager extends TransactionManager {
    /**
     * 根据事务属性开启事务
     * @param definition
     * @return
     * @throws TransactionException
     */
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    /**
     * 提交事务
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
