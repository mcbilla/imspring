package com.mcb.imspring.tx.transaction.td;

/**
 * 默认空事务属性
 */
public final class StaticTransactionDefinition implements TransactionDefinition{
    static final StaticTransactionDefinition INSTANCE = new StaticTransactionDefinition();

    private StaticTransactionDefinition() {
    }
}
