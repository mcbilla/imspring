package com.mcb.imspring.tx.sync;

/**
 * 事务同步器是可以注册到事务处理过程中的回调接口。它就像是事务处理的事件监听器，当事务处理的某些规定时点发生时，会调用 TransactionSynchronization 上的一些方法来执行相应的回调逻辑。
 */
public interface TransactionSynchronization {
    // 事务提交状态
    int STATUS_COMMITTED = 0;

    // 事务回滚状态
    int STATUS_ROLLED_BACK = 1;

    // 系统异常状态
    int STATUS_UNKNOWN = 2;

    // 事务提交之前
    default void beforeCommit(boolean readOnly) {
    }

    // 操作完成之前(包含事务成功或者事务回滚)
    default void beforeCompletion() {
    }

    // 事务成功提交之后
    default void afterCommit() {
    }

    // 操作完成之后(包含事务成功或者事务回滚)
    default void afterCompletion(int status) {
    }
}
