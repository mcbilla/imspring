package com.mcb.imspring.tx.transaction.td;

public class DefaultTransactionDefinition implements TransactionDefinition {
    private int propagationBehavior = PROPAGATION_REQUIRED;

    private int isolationLevel = ISOLATION_DEFAULT;


    public DefaultTransactionDefinition() {
    }

    public DefaultTransactionDefinition(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    @Override
    public int getPropagationBehavior() {
        return propagationBehavior;
    }

    @Override
    public int getIsolationLevel() {
        return isolationLevel;
    }
}
