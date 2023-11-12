package com.mcb.imspring.tx.transaction.ts;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.ts.TransactionCallback;
import com.sun.istack.internal.Nullable;

public interface TransactionOperations {
    @Nullable
    <T> T execute(TransactionCallback<T> action) throws TransactionException;
}
