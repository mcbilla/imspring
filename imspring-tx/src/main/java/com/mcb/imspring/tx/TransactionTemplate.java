package com.mcb.imspring.tx;

import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.td.DefaultTransactionDefinition;
import com.mcb.imspring.tx.transaction.ts.TransactionCallback;
import com.mcb.imspring.tx.transaction.ts.TransactionOperations;

public class TransactionTemplate extends DefaultTransactionDefinition
        implements TransactionOperations, InitializingBean {
    public TransactionTemplate() {
        super();
    }

    public TransactionTemplate(int propagationBehavior) {
        super(propagationBehavior);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return null;
    }
}
