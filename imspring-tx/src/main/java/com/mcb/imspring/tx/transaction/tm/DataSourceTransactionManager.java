package com.mcb.imspring.tx.transaction.tm;

import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.jdbc.ConnectionHolder;
import com.mcb.imspring.tx.jdbc.JdbcTransactionObjectSupport;
import com.mcb.imspring.tx.sync.TransactionSynchronizationManager;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.tm.AbstractPlatformTransactionManager;
import com.mcb.imspring.tx.transaction.ts.DefaultTransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager extends AbstractPlatformTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        JdbcTransactionObjectSupport txObject = (JdbcTransactionObjectSupport) transaction;
        Connection con;

        try {
            con = txObject.getConnectionHolder().getConnection();
            if (con.getAutoCommit()) {
                con.setAutoCommit(false);
            }
            TransactionSynchronizationManager.bindResource(obtainDataSource(), txObject.getConnectionHolder());
        } catch (Throwable ex) {
            throw new TransactionException("Could not open JDBC Connection for transaction", ex);
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        JdbcTransactionObjectSupport txObject = (JdbcTransactionObjectSupport) status.getTransaction();
        Connection con;
        try {
            con = txObject.getConnectionHolder().getConnection();
            logger.debug("Committing JDBC transaction on Connection [" + con + "]");
            con.commit();
        } catch (SQLException ex) {
            throw new TransactionException("JDBC commit failed ", ex);
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        JdbcTransactionObjectSupport txObject = (JdbcTransactionObjectSupport) status.getTransaction();
        Connection con;
        try {
            con = txObject.getConnectionHolder().getConnection();
            logger.debug("Rolling back JDBC transaction on Connection [" + con + "]");
            con.rollback();
        }
        catch (SQLException ex) {
            throw new TransactionException("JDBC rollback failed ", ex);
        }
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        TransactionSynchronizationManager.unbindResource(obtainDataSource());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected DataSource obtainDataSource() {
        DataSource dataSource = getDataSource();
        Assert.state(dataSource != null, "No DataSource set");
        return dataSource;
    }
}
