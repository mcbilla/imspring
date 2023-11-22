package com.mcb.imspring.core.utils;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.exception.BeansException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mcb.imspring.core.utils.ReflectionUtils.findAnnotation;

public abstract class BeanUtils {

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;

    static {
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(boolean.class, false);
        values.put(byte.class, (byte) 0);
        values.put(short.class, (short) 0);
        values.put(int.class, 0);
        values.put(long.class, 0L);
        values.put(float.class, 0F);
        values.put(double.class, 0D);
        values.put(char.class, '\0');
        DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
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

    public static String getBeanName(Method method) {
        String beanName = method.getName();
        if (method.isAnnotationPresent(Bean.class)) {
            Bean anno = method.getAnnotation(Bean.class);
            if (!StringUtils.isEmpty(anno.value())) {
                beanName = anno.value();
            }
        }
        return beanName;
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

    /**
     * 类实例化，默认使用无参构造函数
     */
    public static <T> T instantiateClass(Class<T> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeansException("Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        }
        catch (NoSuchMethodException ex) {
            throw new BeansException("No default constructor found", ex);
        }
    }

    /**
     * 使用指定构造函数实例化
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ctor.setAccessible(true);
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Assert.isTrue(args.length <= parameterTypes.length, "Can't specify more arguments than constructor parameters");
            Object[] argsWithDefaultValues = new Object[args.length];
            for (int i = 0 ; i < args.length; i++) {
                if (args[i] == null) {
                    Class<?> parameterType = parameterTypes[i];
                    argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
                }
                else {
                    argsWithDefaultValues[i] = args[i];
                }
            }
            return ctor.newInstance(argsWithDefaultValues);
        } catch (Exception ex) {
            throw new BeansException(String.format("instantiate fail by constructor %s", ctor.getName()), ex);
        }
    }

    public static ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = BeanUtils.class.getClassLoader();
        }
        return cl;
    }
}
