package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.tx.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * 持有connection对象，可以设置savepoint
 */
public class JdbcTransactionObjectSupport implements SavepointManager{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ConnectionHolder connectionHolder;

    public void setConnectionHolder(ConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public ConnectionHolder getConnectionHolder() {
        Assert.state(this.connectionHolder != null, "No ConnectionHolder available");
        return this.connectionHolder;
    }

    public boolean hasConnectionHolder() {
        return (this.connectionHolder != null);
    }

    protected ConnectionHolder getConnectionHolderForSavepoint() throws TransactionException {
        if (!hasConnectionHolder()) {
            throw new TransactionException(
                    "Cannot create nested transaction when not exposing a JDBC transaction");
        }
        return getConnectionHolder();
    }

    @Override
    public Object createSavepoint() throws TransactionException {
        ConnectionHolder conHolder = getConnectionHolderForSavepoint();
        try {
            return conHolder.createSavepoint();
        } catch (SQLException e) {
            throw new TransactionException("Could not create JDBC savepoint", e);
        }
    }

    @Override
    public void rollbackToSavepoint(Object savepoint) throws TransactionException {
        ConnectionHolder conHolder = getConnectionHolderForSavepoint();
        try {
            conHolder.getConnection().rollback((Savepoint) savepoint);
        } catch (SQLException e) {
            throw new TransactionException("Could not roll back to JDBC savepoint", e);
        }
    }

    @Override
    public void releaseSavepoint(Object savepoint) throws TransactionException {
        ConnectionHolder conHolder = getConnectionHolderForSavepoint();
        try {
            conHolder.getConnection().releaseSavepoint((Savepoint) savepoint);
        }
        catch (Throwable ex) {
            logger.debug("Could not explicitly release JDBC savepoint", ex);
        }
    }
}
