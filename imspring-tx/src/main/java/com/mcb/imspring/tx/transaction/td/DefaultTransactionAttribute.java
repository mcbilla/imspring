package com.mcb.imspring.tx.transaction.td;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 事务属性，保存@Transaction修饰的方法的传播行为、隔离级别、回滚规则、唯一标识等信息
 */
public class DefaultTransactionAttribute extends DefaultTransactionDefinition implements TransactionAttribute{

    // 事务属性唯一标识，默认是方法的全路径名
    private String descriptor;

    // 事务回滚规则
    private List<RollbackRuleAttribute> rollbackRules;

    @Override
    public boolean rollbackOn(Throwable ex) {
        return false;
    }

    public void setRollbackRules(List<RollbackRuleAttribute> rollbackRules) {
        this.rollbackRules = rollbackRules;
    }

    public List<RollbackRuleAttribute> getRollbackRules() {
        if (this.rollbackRules == null) {
            this.rollbackRules = new ArrayList<>();
        }
        return this.rollbackRules;
    }

    public void setDescriptor(@Nullable String descriptor) {
        this.descriptor = descriptor;
    }

    @Nullable
    public String getDescriptor() {
        return this.descriptor;
    }
}
