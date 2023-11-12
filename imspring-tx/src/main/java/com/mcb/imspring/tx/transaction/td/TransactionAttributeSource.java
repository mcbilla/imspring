package com.mcb.imspring.tx.transaction.td;

import com.mcb.imspring.tx.transaction.td.TransactionAttribute;

import java.lang.reflect.Method;

public interface TransactionAttributeSource {
    TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass);
}
