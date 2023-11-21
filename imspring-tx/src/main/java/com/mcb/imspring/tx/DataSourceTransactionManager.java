package com.mcb.imspring.tx;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.tm.PlatformTransactionManager;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;

import javax.sql.DataSource;

public class DataSourceTransactionManager implements PlatformTransactionManager {

    private DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        return null;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {

    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {

    }
}
