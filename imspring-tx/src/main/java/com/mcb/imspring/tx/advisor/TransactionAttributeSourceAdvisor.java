package com.mcb.imspring.tx.advisor;

import com.mcb.imspring.aop.advisor.AbstractPointcutAdvisor;
import com.mcb.imspring.aop.pointcut.ClassFilter;
import com.mcb.imspring.aop.pointcut.Pointcut;
import com.mcb.imspring.core.utils.Assert;
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

    public TransactionAttributeSourceAdvisor() {
    }
    public TransactionAttributeSourceAdvisor(TransactionInterceptor interceptor) {
        setTransactionInterceptor(interceptor);
    }

    public void setTransactionInterceptor(TransactionInterceptor interceptor) {
        this.transactionInterceptor = interceptor;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }


    @Override
    public Advice getAdvice() {
        Assert.state(this.transactionInterceptor != null, "No TransactionInterceptor set");
        return this.transactionInterceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
