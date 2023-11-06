package com.mcb.imspring.tx.transaction;

import com.sun.istack.internal.Nullable;

@FunctionalInterface
public interface TransactionCallback<T> {
    @Nullable
    T doInTransaction(TransactionStatus status);
}
