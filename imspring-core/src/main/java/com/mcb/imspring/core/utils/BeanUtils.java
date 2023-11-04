package com.mcb.imspring.core.utils;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.exception.BeansException;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class BeanUtils {
    /**
     * 查找指定类的指定注解，这里需要递归查找注解的注解
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement element, Class<A> targetAnno) {
        if (element.isAnnotationPresent(targetAnno)) {
            return element.getAnnotation(targetAnno);
        }
        for (Annotation anno : element.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!annoType.getPackage().getName().equals("java.lang.annotation")) {
                return findAnnotation(annoType, targetAnno);
            }
        }
        return null;
    }

    /**
     * 获取bean名称，优先使用注解的值，如果没有设置注解值，默认使用小写开头的类名
     */
    public static String getBeanName(Class<?> clazz) {
        for (Annotation anno : clazz.getAnnotations()) {
            if (findAnnotation(anno.annotationType(), Component.class) != null) {
                try {
                    String name = (String) anno.annotationType().getMethod("value").invoke(anno);
                    if (name != null && name.length() > 0) {
                        return name;
                    }
                } catch (ReflectiveOperationException e) {
                    throw new BeansException("Cannot get annotation value.", e);
                }
            }
        }
        return getBeanName(clazz.getSimpleName());
    }

    public static String getBeanName(String className) {
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    /**
     * 从候选的构造函数中返回合适的构造函数
     * 1、只有一个构造函数的时候，将会直接使用这个构造函数来进行初始化。无论这个构造函数究竟是含参的还是不含参的。
     * 2、有多个构造函数的情况下，如果构造函数中有无参构造函数，那么使用无参构造函数进行初始化。如果没有无参构造函数，则直接抛出异常。
     */
    public static Constructor getBeanConstructor(Class<?> clazz) {
        Constructor<?>[] cons = clazz.getDeclaredConstructors();
        if (cons.length == 0) {
            throw new BeansException("At least one constructor must define in class " + clazz.getName());
        }
        Constructor res = null;
        if (cons.length == 1) {
            res = cons[0];
        } else {
            for (Constructor con : cons) {
                if (con.getParameterCount() == 0) {
                    res = con;
                }
            }
        }
        if (res == null) {
            throw new BeansException("More than one constructor found in class, non-arg constructor must define " + clazz.getName());
        }
        return res;
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    @Nullable
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return findAnnotation(element, annotationType) != null;
    }


}
