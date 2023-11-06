package com.mcb.imspring.tx.transaction;

import com.mcb.imspring.tx.exception.TransactionException;
import com.sun.istack.internal.Nullable;

public interface PlatformTransactionManager extends TransactionManager {
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    void commit(TransactionStatus status) throws TransactionException;

    void rollback(TransactionStatus status) throws TransactionException;
}
