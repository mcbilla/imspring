package com.mcb.imspring.tx.transaction.td;

import java.lang.reflect.Method;

public interface TransactionAttributeSource {

    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass);
}
