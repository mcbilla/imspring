package com.mcb.imspring.tx.transaction.td;

import com.mcb.imspring.core.common.Constants;
import com.sun.istack.internal.Nullable;

public class DefaultTransactionDefinition implements TransactionDefinition {

    public static final String PREFIX_PROPAGATION = "PROPAGATION_";

    public static final String PREFIX_ISOLATION = "ISOLATION_";

    private int propagationBehavior = PROPAGATION_REQUIRED;

    private int isolationLevel = ISOLATION_DEFAULT;

    private String name;

    static final Constants constants = new Constants(TransactionDefinition.class);

    public DefaultTransactionDefinition() {
    }

    public DefaultTransactionDefinition(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    public final void setPropagationBehavior(int propagationBehavior) {
        if (!constants.getValues(PREFIX_PROPAGATION).contains(propagationBehavior)) {
            throw new IllegalArgumentException("Only values of propagation constants allowed");
        }
        this.propagationBehavior = propagationBehavior;
    }

    public void setIsolationLevel(int isolationLevel) {
        if (!constants.getValues(PREFIX_ISOLATION).contains(isolationLevel)) {
            throw new IllegalArgumentException("Only values of isolation constants allowed");
        }
        this.isolationLevel = isolationLevel;
    }

    @Override
    public int getPropagationBehavior() {
        return propagationBehavior;
    }

    @Override
    public int getIsolationLevel() {
        return isolationLevel;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public final String getName() {
        return this.name;
    }
}
