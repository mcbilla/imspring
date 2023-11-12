package com.mcb.imspring.tx.transaction.td;

public interface TransactionAttribute extends TransactionDefinition{
    boolean rollbackOn(Throwable ex);
}
