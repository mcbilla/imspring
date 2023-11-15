package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.ExposeInvocationInterceptor;
import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.joinpoint.ProxyMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.core.common.Ordered;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Advice 对 Joinpoint 执行的某些增强操作
 * 例如 JDK 动态代理使用的 InvocationHandler、cglib 使用的 MethodInterceptor，在抽象概念上可以算是 Advice（即使它们没有继承Advice）。
 * 主要使用 Advice 的子接口--MethodInterceptor。
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
public abstract class AbstractAspectJAdvice implements AspectJAdvice, Ordered, Comparable<Advice> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String aspectName;

    private final String aspectJMethodName;

    private final Class<?> aspectJClass;

    private final Class<?>[] aspectJParameterTypes;

    protected final Method aspectJAdviceMethod;

    private final Object aspectJBean;

    private final AspectJExpressionPointcut pointcut;

    protected static final String JOIN_POINT_KEY = JoinPoint.class.getName();

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, String aspectName, Object aspectJBean) {
        this.aspectName = aspectName;
        this.aspectJBean = aspectJBean;
        this.aspectJClass = aspectJAdviceMethod.getDeclaringClass();
        this.aspectJMethodName = aspectJAdviceMethod.getName();
        this.aspectJParameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
    }

    /**
     * 从 ThreadLocal 获取当前执行到的 JoinPoint
     */
    protected JoinPoint getJoinPoint() {
        return currentJoinPoint();
    }

    public static JoinPoint currentJoinPoint() {
        MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        JoinPoint jp = (JoinPoint) pmi.getUserAttribute(JOIN_POINT_KEY);
        if (jp == null) {
            jp = new MethodInvocationProceedingJoinPoint(pmi);
            pmi.setUserAttribute(JOIN_POINT_KEY, jp);
        }
        return jp;
    }

    protected Object invokeAdviceMethod(JoinPoint jp, Object returnValue, Throwable ex) throws Throwable {
        return invokeAdviceMethodWithGivenArgs(argBinding(jp, returnValue, ex));
    }

    protected Object invokeAdviceMethod(Object returnValue, Throwable ex) throws Throwable {
        return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), returnValue, ex));
    }

    /**
     * Advice 参数绑定，JoinPoint 类型的参数必须是第一位
     */
    protected Object[] argBinding(JoinPoint jp, Object returnValue, Throwable ex) {
        Object[] adviceInvocationArgs = new Object[this.aspectJParameterTypes.length];
        if (adviceInvocationArgs.length == 0) {
            return adviceInvocationArgs;
        }
        int numBound = 0;
        if (JoinPoint.class.isAssignableFrom(this.aspectJParameterTypes[0])) {
            adviceInvocationArgs[0] = jp;
            numBound++;
        }
        if (numBound != this.aspectJParameterTypes.length) {
            throw new IllegalStateException("Required to bind " + this.aspectJParameterTypes.length +
                    " arguments, but only bound " + numBound + " (JoinPointMatch " + " bound in invocation)");
        }

        return adviceInvocationArgs;
    }

    protected Object invokeAdviceMethodWithGivenArgs(Object[] args) throws Throwable {
        Object[] actualArgs = args;
        if (this.aspectJAdviceMethod.getParameterCount() == 0) {
            actualArgs = null;
        }
        return this.aspectJAdviceMethod.invoke(this.aspectJBean, actualArgs);
    }

    @Override
    public abstract int getOrder();

    @Override
    public int compareTo(Advice advice) {
        return this.getOrder() - ((Ordered)advice).getOrder();
    }

    public AspectJExpressionPointcut getPointcut() {
        return pointcut;
    }

    @Override
    public String getAspectName() {
        return this.aspectName;
    }

    @Override
    public String getAspectMethodName() {
        return this.aspectJMethodName;
    }

    public Method getAspectJMethod() {
        return aspectJAdviceMethod;
    }
}
