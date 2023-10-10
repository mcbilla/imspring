package com.mcb.imspring.core.utils;

import java.lang.annotation.Annotation;

public class BeanUtils {
    public static <A extends Annotation> A findAnnotation(Class<?> target, Class<A> annoClass) {
        return target.getAnnotation(annoClass);
    }
}
