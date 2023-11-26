package com.mcb.imspring.core.annotation;

import java.lang.annotation.*;

/**
 * @Import 注解提供了 @Bean 注解的功能，相比起 @Bean 可以更快导入第三方的类到 bean 容器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

    Class<?>[] value();

}
