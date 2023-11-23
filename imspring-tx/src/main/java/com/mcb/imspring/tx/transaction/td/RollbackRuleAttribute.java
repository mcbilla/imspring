package com.mcb.imspring.tx.transaction.td;

import com.mcb.imspring.core.utils.Assert;

/**
 * 事务回滚规则，规定哪些情况下需要回滚
 * 默认情况下遇到 RuntimeException 才需要回滚
 */
public class RollbackRuleAttribute {
    public static final RollbackRuleAttribute ROLLBACK_ON_RUNTIME_EXCEPTIONS =
            new RollbackRuleAttribute(RuntimeException.class);

    // 需要回滚的异常名称
    // 为什么不直接存异常类型Class，而是存异常名称字符串呢？因为Spring的@Transactional还有一个属性rollbackForClassName，存的是异常名称字符串
    // 这里存为字符串，后面不管是匹配异常类型还是匹配异常类型字符串，统一把目标异常类型字符串传进来，通过递归查找是否匹配
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
    public int getDepth(Throwable exception) {
        return getDepth(exception.getClass(), 0);
    }

    private int getDepth(Class<?> exceptionType, int depth) {
        if (exceptionType.getName().contains(this.exceptionPattern)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionType == Throwable.class) {
            return -1;
        }
        return getDepth(exceptionType.getSuperclass(), depth + 1);
    }

    public String getExceptionName() {
        return this.exceptionPattern;
    }

}
