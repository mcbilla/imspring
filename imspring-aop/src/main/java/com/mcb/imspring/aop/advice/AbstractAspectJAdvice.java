package com.mcb.imspring.aop.advice;

import com.mcb.imspring.core.collections.Ordered;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 所有的增强通知都是 MethodInterceptor 的子类，MethodInterceptor 是 aspectJ 提供的拦截器接口
 *
 * 五种通知的执行顺序
 * public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 *    Object result;
 *    try {
 *        //@Around前置处理
 *        //@Before
 *        result = method.invoke(target, args);
 *        //@After
 *        //@Around后置处理
 *        return result;
 *    } catch (InvocationTargetException e) {
 *        Throwable targetException = e.getTargetException();
 *        //@AfterThrowing
 *        throw targetException;
 *    } finally {
 *        //@AfterReturning
 *    }
 * }
 */
public abstract class AbstractAspectJAdvice implements Advice, Ordered, MethodInterceptor, Comparable<Advice> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<?> aspectJClass;

    private final String aspectJMethodName;

    private final Class<?>[] aspectJParameterTypes;

    protected final Method aspectJAdviceMethod;
    private final AspectJExpressionPointcut pointcut;

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut) {
        this.aspectJClass = aspectJAdviceMethod.getDeclaringClass();
        this.aspectJMethodName = aspectJAdviceMethod.getName();
        this.aspectJParameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
    }

    protected Object invokeAdviceMethod(JoinPoint jp, Object returnValue, Throwable t) throws InvocationTargetException, IllegalAccessException {
        Object[] actualArgs = new Object[this.aspectJParameterTypes.length];
        if (actualArgs.length == 0) {
            actualArgs = null;
        } else if (actualArgs.length == 1 && this.aspectJParameterTypes[0].isAssignableFrom(ProceedingJoinPoint.class)) {
            actualArgs[0] = jp;
        }
        this.aspectJAdviceMethod.setAccessible(true);
        return this.aspectJAdviceMethod.invoke(jp.getThis(), actualArgs);
    }

    @Override
    public abstract int getOrder();

    /**
     * 当前对象（调用这个方法的对象）和形参对象（被比较的另一个对象）进行比较
     * a - b < 0，说明 a < b，a排前面，b排后面
     * a - b > 0，说明 a > b，b排前面，a排后面
     */
    @Override
    public int compareTo(Advice advice) {
        return this.getOrder() - ((Ordered)advice).getOrder();
    }

    public AspectJExpressionPointcut getPointcut() {
        return pointcut;
    }
}
