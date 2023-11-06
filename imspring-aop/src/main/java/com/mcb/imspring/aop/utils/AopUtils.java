package com.mcb.imspring.aop.utils;

import com.mcb.imspring.core.utils.Assert;

public abstract class AopUtils {

    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    public static Class<?> getTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        return (isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
    }

    public static boolean isCglibProxy(Object object) {
        return object.getClass().getName().contains(CGLIB_CLASS_SEPARATOR);
    }

}
