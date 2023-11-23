package com.mcb.imspring.tx.transaction.tm;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.ts.DefaultTransactionStatus;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;

public abstract class AbstractPlatformTransactionManager implements PlatformTransactionManager{
    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        TransactionDefinition def = (definition != null ? definition : TransactionDefinition.withDefaults());

        Object transaction = doGetTransaction();

        return null;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {

    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {

    }

    protected abstract Object doGetTransaction() throws TransactionException;

    protected abstract void doCommit(DefaultTransactionStatus status) throws TransactionException;

    protected abstract void doRollback(DefaultTransactionStatus status) throws TransactionException;
}
