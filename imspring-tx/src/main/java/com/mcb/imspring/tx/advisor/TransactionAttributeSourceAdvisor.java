package com.mcb.imspring.tx.advisor;

import com.mcb.imspring.aop.advisor.AbstractPointcutAdvisor;
import com.mcb.imspring.aop.pointcut.Pointcut;
import com.mcb.imspring.tx.TransactionInterceptor;
import com.mcb.imspring.tx.pointcut.TransactionAttributeSourcePointcut;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;
import org.aopalliance.aop.Advice;

public class TransactionAttributeSourceAdvisor extends AbstractPointcutAdvisor {

    private TransactionInterceptor transactionInterceptor;

    private final TransactionAttributeSourcePointcut pointcut = new TransactionAttributeSourcePointcut() {
        @Override
        protected TransactionAttributeSource getTransactionAttributeSource() {
            return (transactionInterceptor != null ? transactionInterceptor.getTransactionAttributeSource() : null);
        }
    };

    @Override
    public Advice getAdvice() {
        return null;
    }

    @Override
    public Pointcut getPointcut() {
        return null;
    }
}
