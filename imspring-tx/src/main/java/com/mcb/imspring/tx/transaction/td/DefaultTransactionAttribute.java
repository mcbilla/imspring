package com.mcb.imspring.tx.transaction.td;

import java.util.ArrayList;
import java.util.List;

public class DefaultTransactionAttribute extends DefaultTransactionDefinition implements TransactionAttribute{

    /**
     * 事务回滚规则
     */
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
}
