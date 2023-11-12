package com.mcb.imspring.tx.transaction.tm;


import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.ts.TransactionCallback;

/**
 * CallbackPreferringPlatformTransactionManager 多一个 execute 方法，用于执行事务方法并回调
 */
public interface CallbackPreferringPlatformTransactionManager extends PlatformTransactionManager{
    <T> T execute(TransactionDefinition definition, TransactionCallback<T> callback) throws TransactionException;
}
