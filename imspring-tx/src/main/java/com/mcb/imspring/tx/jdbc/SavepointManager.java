package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.tx.exception.TransactionException;

public interface SavepointManager {
    Object createSavepoint() throws TransactionException;

    void rollbackToSavepoint(Object savepoint) throws TransactionException;

    void releaseSavepoint(Object savepoint) throws TransactionException;
}
