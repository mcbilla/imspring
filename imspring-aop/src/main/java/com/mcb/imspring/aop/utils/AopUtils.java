package com.mcb.imspring.aop.utils;

import com.mcb.imspring.aop.advisor.AspectJExpressionPointcutAdvisor;
import com.mcb.imspring.aop.proxy.SpringProxy;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.ReflectionUtils;
import org.aspectj.lang.annotation.Aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public abstract class AopUtils {

    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    public static final String JDK_CLASS_SEPARATOR = "$Proxy";

    public static Class<?> getTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        return (isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
    }

    public static boolean isJdkProxy(Object object) {
        return object != null && object.getClass().getName().contains(JDK_CLASS_SEPARATOR);

    }

    public static boolean isCglibProxy(Object object) {
        return object != null && object.getClass().getName().contains(CGLIB_CLASS_SEPARATOR);
    }

    public static boolean isProxy(Object object) {
        return object instanceof SpringProxy || isJdkProxy(object) || isCglibProxy(object);
    }

    public static boolean isAspect(Class<?> beanClass) {
        return ReflectionUtils.hasAnnotation(beanClass, Aspect.class);
    }

    public static boolean isAdvice(Method method) {
        Set<Class<? extends Annotation>> aspectJAnnoTypes = AspectJExpressionPointcutAdvisor.AspectJAnnotation.annotationTypeMap.keySet();
        for (Class<? extends Annotation> anno : aspectJAnnoTypes) {
            if (method.isAnnotationPresent(anno)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInterfaceProxied(Class<?> proxyIntf, Class<?>[] intfs) {
        for (Class<?> intf : intfs) {
            if (proxyIntf.isAssignableFrom(intf)) {
                return true;
            }
        }
        return false;
    }

}
