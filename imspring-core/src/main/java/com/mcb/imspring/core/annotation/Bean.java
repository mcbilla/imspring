package com.mcb.imspring.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    /**
     * Bean name. default to method name.
     */
    String value() default "";

    String initMethod() default "";

    String destroyMethod() default "";
}
