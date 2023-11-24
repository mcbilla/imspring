package com.mcb.imspring.tx.transaction.tm;

import com.mcb.imspring.tx.exception.TransactionException;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.ts.DefaultTransactionStatus;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlatformTransactionManager implements PlatformTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        TransactionDefinition def = (definition != null ? definition : TransactionDefinition.withDefaults());
        Object transaction = doGetTransaction();

        if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
            throw new TransactionException(
                    "No existing transaction found for transaction marked with propagation 'mandatory'");
        } else if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
                def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
                def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
            logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);
            return startTransaction(def, transaction);
        } else {
            throw new TransactionException(String.format("Unsupport transaction propagation behavior " + def.getPropagationBehavior()));
        }
    }

    private TransactionStatus startTransaction(TransactionDefinition def, Object transaction) {
        DefaultTransactionStatus status = new DefaultTransactionStatus(transaction);
        doBegin(transaction, def);
        return status;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
        if (defStatus.hasSavepoint()) {
            logger.debug("Releasing transaction savepoint");
            defStatus.releaseHeldSavepoint();
        }
        try {
            doCommit(defStatus);
        } finally {
            doCleanupAfterCompletion(defStatus);
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
        if (defStatus.hasSavepoint()) {
            logger.debug("Rolling back transaction to savepoint");
            defStatus.rollbackToHeldSavepoint();
        }
        try {
            doRollback(defStatus);
        } finally {
            doCleanupAfterCompletion(defStatus);
        }

    }

    protected abstract Object doGetTransaction() throws TransactionException;

    protected abstract void doBegin(Object transaction, TransactionDefinition definition)
            throws TransactionException;

    protected abstract void doCommit(DefaultTransactionStatus status) throws TransactionException;

    protected abstract void doRollback(DefaultTransactionStatus status) throws TransactionException;

    protected void doCleanupAfterCompletion(Object transaction) {
    }
}
