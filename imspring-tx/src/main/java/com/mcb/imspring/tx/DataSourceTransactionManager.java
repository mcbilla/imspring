package com.mcb.imspring.tx;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.jdbc.ConnectionHolder;
import com.mcb.imspring.tx.jdbc.JdbcTransactionObjectSupport;
import com.mcb.imspring.tx.transaction.tm.AbstractPlatformTransactionManager;
import com.mcb.imspring.tx.transaction.tm.PlatformTransactionManager;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.ts.DefaultTransactionStatus;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DataSourceTransactionManager extends AbstractPlatformTransactionManager {

    private DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected Object doGetTransaction() throws TransactionException {
        JdbcTransactionObjectSupport txObject = new JdbcTransactionObjectSupport();
        try {
            txObject.setConnectionHolder(new ConnectionHolder(dataSource.getConnection()));
        } catch (SQLException e) {
            throw new TransactionException("Could not get ConnectionHolder ", e);
        }
        return txObject;
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {

    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {

    }
}
