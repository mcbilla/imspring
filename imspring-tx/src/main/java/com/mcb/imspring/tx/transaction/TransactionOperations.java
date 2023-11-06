package com.mcb.imspring.tx.transaction;

import com.mcb.imspring.tx.exception.TransactionException;
import com.sun.istack.internal.Nullable;

public interface TransactionOperations {
    @Nullable
    <T> T execute(TransactionCallback<T> action) throws TransactionException;
}
