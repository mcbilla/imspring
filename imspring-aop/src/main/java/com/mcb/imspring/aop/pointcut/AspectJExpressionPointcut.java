package com.mcb.imspring.aop.pointcut;

import com.sun.istack.internal.Nullable;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 目前只支持方法级别的匹配
 */
public class AspectJExpressionPointcut implements MethodMatcher, Pointcut, ClassFilter {

    private PointcutParser pointcutParser;

    private PointcutExpression pointcutExpression;

    private String expression;

    private static final Set<PointcutPrimitive> DEFAULT_SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

    public AspectJExpressionPointcut() {
        this(DEFAULT_SUPPORTED_PRIMITIVES);
    }

    public AspectJExpressionPointcut(Set<PointcutPrimitive> supportedPrimitives) {
        pointcutParser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(supportedPrimitives);
    }

    /**
     * 使用 AspectJ Expression 匹配类，这里只是可能会匹配成功
     * @param targetClass
     * @return
     */
    @Override
    public boolean matchers(Class targetClass) {
        obtainPointcutExpression();
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * 使用 AspectJ Expression 匹配方法
     * @param method
     * @param targetClass
     * @return
     */
    @Override
    public boolean matchers(Method method, Class<?> targetClass) {
        obtainPointcutExpression();
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        // Special handling for this, target, @this, @target, @annotation
        // in Spring - we can optimize since we know we have exactly this class,
        // and there will never be matching subclass at runtime.
        // https://github.com/spring-projects/spring-framework/blob/master/spring-aop/src/main/java/org/springframework/aop/aspectj/AspectJExpressionPointcut.java
        if (shadowMatch.alwaysMatches()) {
            return true;
        }
        else if (shadowMatch.neverMatches()) {
            return false;
        }
        return false;
    }

    private void obtainPointcutExpression() {
        if (getExpression() == null) {
            throw new IllegalStateException("Must set property 'expression' before attempting to match");
        }
        if (pointcutExpression == null) {
            pointcutExpression = pointcutParser.parsePointcutExpression(getExpression());
        }
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Nullable
    public String getExpression() {
        return this.expression;
    }
}
