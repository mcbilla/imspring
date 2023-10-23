package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.advisor.Ordered;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 五种通知的执行顺序
 * public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 *    Object result;
 *    try {
 *        //@Before
 *        result = method.invoke(target, args);
 *        //@After
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
        this.aspectJAdviceMethod.setAccessible(true);
        Object[] actualArgs = jp.getArgs();
        if (this.aspectJAdviceMethod.getParameterCount() == 0) {
            actualArgs = null;
        }
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
}