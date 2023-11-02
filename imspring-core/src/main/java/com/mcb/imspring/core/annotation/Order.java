package com.mcb.imspring.core.annotation;

import com.mcb.imspring.core.collections.Ordered;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Order {

    int value() default Ordered.DEFAULT_PRECEDENCE;

}
