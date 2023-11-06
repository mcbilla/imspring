package com.mcb.imspring.tx;

import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.DefaultTransactionDefinition;
import com.mcb.imspring.tx.transaction.TransactionCallback;
import com.mcb.imspring.tx.transaction.TransactionOperations;

public class TransactionTemplate extends DefaultTransactionDefinition
        implements TransactionOperations, InitializingBean {
    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return null;
    }
}
