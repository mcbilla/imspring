package com.mcb.imspring.tx.transaction.td;

import com.mcb.imspring.core.utils.Assert;

/**
 * 事务回滚规则，规定哪些情况下需要回滚
 * 默认情况下遇到 RuntimeException 才需要回滚
 */
public class RollbackRuleAttribute {
    public static final RollbackRuleAttribute ROLLBACK_ON_RUNTIME_EXCEPTIONS =
            new RollbackRuleAttribute(RuntimeException.class);

    private final String exceptionPattern;

    public RollbackRuleAttribute(Class<?> exceptionType) {
        Assert.notNull(exceptionType, "'exceptionType' cannot be null");
        if (!Throwable.class.isAssignableFrom(exceptionType)) {
            throw new IllegalArgumentException(
                    "Cannot construct rollback rule from [" + exceptionType.getName() + "]: it's not a Throwable");
        }
        this.exceptionPattern = exceptionType.getName();
    }
    public RollbackRuleAttribute(String exceptionPattern) {
        Assert.hasText(exceptionPattern, "'exceptionPattern' cannot be null or empty");
        this.exceptionPattern = exceptionPattern;
    }

    public String getExceptionName() {
        return this.exceptionPattern;
    }

}
