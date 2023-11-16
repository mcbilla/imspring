package com.mcb.imspring.tx.pointcut;

import com.mcb.imspring.aop.pointcut.AbstractPointcut;
import com.mcb.imspring.aop.pointcut.ClassFilter;
import com.mcb.imspring.aop.pointcut.MethodMatcher;
import com.mcb.imspring.aop.pointcut.impl.TrueClassFilter;
import com.mcb.imspring.tx.proxy.TransactionalProxy;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;
import com.mcb.imspring.tx.transaction.tm.TransactionManager;

import java.lang.reflect.Method;

public abstract class TransactionAttributeSourcePointcut extends AbstractPointcut {

    private ClassFilter classFilter = TrueClassFilter.INSTANCE;

    protected TransactionAttributeSourcePointcut() {
        setClassFilter(new TransactionAttributeSourceClassFilter());
    }

    @Override
    public boolean matches(Class<?> beanClass) {
        throw new UnsupportedOperationException("Illegal MethodMatcher usage");
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        TransactionAttributeSource tas = getTransactionAttributeSource();
        return (tas == null || tas.getTransactionAttribute(method, targetClass) != null);
    }

    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    protected abstract TransactionAttributeSource getTransactionAttributeSource();

    private class TransactionAttributeSourceClassFilter implements ClassFilter {

        @Override
        public boolean matches(Class<?> clazz) {
            if (TransactionalProxy.class.isAssignableFrom(clazz) ||
                    TransactionManager.class.isAssignableFrom(clazz)) {
                return false;
            }
            TransactionAttributeSource tas = getTransactionAttributeSource();
            return (tas == null || tas.isCandidateClass(clazz));
        }
    }
}
