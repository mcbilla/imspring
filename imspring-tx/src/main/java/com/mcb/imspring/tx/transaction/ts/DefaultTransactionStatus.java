package com.mcb.imspring.tx.transaction.ts;

import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.jdbc.SavepointManager;
import com.sun.istack.internal.Nullable;

public class DefaultTransactionStatus implements TransactionStatus{

    private final Object transaction;

    private Object savepoint;

    public DefaultTransactionStatus(Object transaction) {
        this.transaction = transaction;
    }

    public Object getTransaction() {
        Assert.state(this.transaction != null, "No transaction active");
        return this.transaction;
    }

    @Override
    public boolean hasSavepoint() {
        return false;
    }

    protected void setSavepoint(@Nullable Object savepoint) {
        this.savepoint = savepoint;
    }
    @Nullable
    protected Object getSavepoint() {
        return this.savepoint;
    }

    protected SavepointManager getSavepointManager() {
        return (SavepointManager) this.transaction;
    }

    public void releaseHeldSavepoint() throws TransactionException {
        Object savepoint = getSavepoint();
        if (savepoint == null) {
            throw new TransactionException(
                    "Cannot release savepoint - no savepoint associated with current transaction");
        }
        getSavepointManager().releaseSavepoint(savepoint);
        setSavepoint(null);
    }

    public void rollbackToHeldSavepoint() throws TransactionException {
        Object savepoint = getSavepoint();
        if (savepoint == null) {
            throw new TransactionException(
                    "Cannot roll back to savepoint - no savepoint associated with current transaction");
        }
        getSavepointManager().rollbackToSavepoint(savepoint);
        getSavepointManager().releaseSavepoint(savepoint);
        setSavepoint(null);
    }
}
