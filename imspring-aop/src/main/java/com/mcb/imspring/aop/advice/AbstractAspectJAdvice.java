package com.mcb.imspring.aop.advice;

import com.mcb.imspring.aop.joinpoint.ExposeInvocationInterceptor;
import com.mcb.imspring.aop.joinpoint.MethodInvocationProceedingJoinPoint;
import com.mcb.imspring.aop.joinpoint.ProxyMethodInvocation;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.core.common.Ordered;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.JoinPoint;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.PointcutParameter;
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
    protected MethodInvocationProceedingJoinPoint getJoinPoint() {
        return null;
    }

    protected JoinPointMatch getJoinPointMatch() {
        ProxyMethodInvocation pmi = (ProxyMethodInvocation)ExposeInvocationInterceptor.currentInvocation();
        String expression = this.pointcut.getExpression();
        return (expression != null ? (JoinPointMatch) pmi.getUserAttribute(expression) : null);
    }

    protected Object invokeAdviceMethod(JoinPoint jp, JoinPointMatch jpMatch, Object returnValue, Throwable ex) throws Throwable {
        return invokeAdviceMethodWithGivenArgs(argBinding(jp, jpMatch, returnValue, ex));
    }

    protected Object invokeAdviceMethod(JoinPointMatch jpMatch, Object returnValue, Throwable ex) throws Throwable {
        return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), jpMatch, returnValue, ex));
    }

    /**
     * Advice 参数绑定，JoinPoint 类型的参数必须是第一位
     */
    protected Object[] argBinding(JoinPoint jp, JoinPointMatch jpMatch, Object returnValue, Throwable ex) {
        int numUnboundArgs = this.aspectJParameterTypes.length;
        Object[] adviceInvocationArgs = new Object[numUnboundArgs];
        if (numUnboundArgs == 0) {
            return adviceInvocationArgs;
        }
        if (JoinPoint.class.isAssignableFrom(this.aspectJParameterTypes[0])) {
            adviceInvocationArgs[0] = jp;
            numUnboundArgs--;
        }
        if (numUnboundArgs > 0) {
            PointcutParameter[] parameterBindings = jpMatch.getParameterBindings();
            for (PointcutParameter parameter : parameterBindings) {
                adviceInvocationArgs[numUnboundArgs++] = parameter.getBinding();
            }
        }
        if (numUnboundArgs != this.aspectJParameterTypes.length) {
            throw new IllegalStateException("Required to bind " + this.aspectJParameterTypes.length +
                    " arguments, but only bound " + numUnboundArgs + " (JoinPointMatch " +
                    (jpMatch == null ? "was NOT" : "WAS") + " bound in invocation)");
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
