package com.mcb.imspring.tx.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {
    // 事务管理器，目前只支持 DataSourceTransactionManager
    String value() default "";

    // 事务策略
    Propagation propagation() default Propagation.REQUIRED;

    // 事务隔离级别
    Isolation isolation() default Isolation.DEFAULT;

    // 哪些异常需要进行回滚
    Class<? extends Throwable>[] rollbackFor() default {};
}
