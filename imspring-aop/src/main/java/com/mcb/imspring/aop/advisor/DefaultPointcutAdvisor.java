package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.pointcut.ClassFilter;
import com.mcb.imspring.aop.pointcut.MethodMatcher;
import com.mcb.imspring.aop.pointcut.Pointcut;
import org.aopalliance.aop.Advice;

import java.io.Serializable;
import java.lang.reflect.Method;

public class DefaultPointcutAdvisor extends AbstractPointcutAdvisor{
    private Pointcut pointcut = TruePointcut.INSTANCE;

    public DefaultPointcutAdvisor() {
    }

    public DefaultPointcutAdvisor(Advice advice) {
        this(TruePointcut.INSTANCE, advice);
    }

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        setAdvice(advice);
    }


    public void setPointcut(Pointcut pointcut) {
        this.pointcut = (pointcut != null ? pointcut : TruePointcut.INSTANCE);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }


    @Override
    public String toString() {
        return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice [" + getAdvice() + "]";
    }
}

final class TruePointcut implements Pointcut, Serializable {

    public static final TruePointcut INSTANCE = new TruePointcut();

    /**
     * Enforce Singleton pattern.
     */
    private TruePointcut() {
    }

    @Override
    public ClassFilter getClassFilter() {
        return TrueClassFilter.INSTANCE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return TrueMethodMatcher.INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Pointcut.TRUE";
    }

}

final class TrueMethodMatcher implements MethodMatcher {

    public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

    private TrueMethodMatcher() {
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String toString() {
        return "MethodMatcher.TRUE";
    }

    private Object readResolve() {
        return INSTANCE;
    }

}

final class TrueClassFilter implements ClassFilter{

    public static final TrueClassFilter INSTANCE = new TrueClassFilter();

    private TrueClassFilter() {
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return true;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "ClassFilter.TRUE";
    }

}