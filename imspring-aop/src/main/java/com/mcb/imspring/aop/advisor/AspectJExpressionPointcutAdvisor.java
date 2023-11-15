package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.advice.*;
import com.mcb.imspring.aop.exception.AopConfigException;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.aop.pointcut.Pointcut;
import com.mcb.imspring.core.common.Ordered;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Advice 是通知，Advisor 是增强器，Advisor 和 Advice 是一一对应关系。
 * Advisor = Advice Filter + Advice，这里的 Advice Filter 是 Pointcut。
 */
public class AspectJExpressionPointcutAdvisor extends AbstractPointcutAdvisor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AspectJExpressionPointcut pointcut;

    public AspectJExpressionPointcutAdvisor(Method aspectJAdviceMethod, Object aspectJBean, String aspectName) {
        this.pointcut = instantiatePointcut(aspectJAdviceMethod, aspectJAdviceMethod.getDeclaringClass());
        setAdvice(instantiateAdvice(aspectJAdviceMethod, this.pointcut, aspectName, aspectJBean));
    }

    private AspectJExpressionPointcut instantiatePointcut(Method aspectJAdviceMethod, Class<?> declaringClass) {
        String expresion = null;
        if (aspectJAdviceMethod.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class)) {
            // Pointcut注解，直接返回value
            expresion = aspectJAdviceMethod.getAnnotation(org.aspectj.lang.annotation.Pointcut.class).value();
        } else {
            // 非Pointcut注解，先获取注解value，然后查找和注解value同名的方法，返回方法的Pointcut注解的value
            Annotation[] annotations = aspectJAdviceMethod.getAnnotations();
            String pointcutValue = null;
            for (Annotation annotation : annotations) {
                if (AspectJAnnotation.containsAspectJAnnotation(annotation)) {
                    AspectJAnnotation aspectJAnnotation = new AspectJAnnotation(annotation);
                    pointcutValue = aspectJAnnotation.getAnnotationValue();
                    break;
                }
            }
            if (pointcutValue == null) {
                throw new AopConfigException(String.format("pointcut can not be null %s", declaringClass.getName()));
            }
            Method[] declaredMethods = declaringClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class) && (method.getName() + "()").equals(pointcutValue)) {
                    expresion = method.getAnnotation(org.aspectj.lang.annotation.Pointcut.class).value();
                    break;
                }
            }
        }

        if (expresion == null) {
            throw new AopConfigException(String.format("can not find pointcut or pointcut expression is null %s", declaringClass.getName()));
        }
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expresion);
        return aspectJExpressionPointcut;
    }

    private Advice instantiateAdvice(Method method, AspectJExpressionPointcut pointcut, String aspectName, Object aspectJBean) {
        Advice advice = null;
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (AspectJAnnotation.containsAspectJAnnotation(annotation)) {
                AspectJAnnotation aspectJAnnotation = new AspectJAnnotation(annotation);
                switch (aspectJAnnotation.getAnnotationType()) {
                    case AtPointcut:
                        logger.debug("Processing pointcut name: [{}]，value: [{}]", method.getName(), ((org.aspectj.lang.annotation.Pointcut)annotation).value());
                        break;
                    case AtAround:
                        advice = new AspectJAroundAdvice(method, pointcut, aspectName, aspectJBean);
                        break;
                    case AtBefore:
                        advice = new AspectJMethodBeforeAdvice(method, pointcut, aspectName, aspectJBean);
                        break;
                    case AtAfter:
                        advice = new AspectJAfterAdvice(method, pointcut, aspectName, aspectJBean);
                        break;
                    case AtAfterReturning:
                        advice = new AspectJAfterReturningAdvice(method, pointcut, aspectName, aspectJBean);
                        break;
                    case AtAfterThrowing:
                        advice = new AspectJAfterThrowingAdvice(method, pointcut, aspectName, aspectJBean);
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "Unsupported advice type on method: " + method);
                }
            }
        }
        return (advice != null ? advice : EMPTY_ADVICE);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    protected enum AspectJAnnotationType {
        AtPointcut, AtAround, AtBefore, AtAfter, AtAfterReturning, AtAfterThrowing
    }

    public static class AspectJAnnotation<A extends Annotation> {
        private static final String[] EXPRESSION_ATTRIBUTES = new String[] {"pointcut", "value"};

        public static Map<Class<? extends Annotation>, AspectJAnnotationType> annotationTypeMap = new HashMap<>(8);

        static {
            annotationTypeMap.put(org.aspectj.lang.annotation.Pointcut.class, AspectJAnnotationType.AtPointcut);
            annotationTypeMap.put(Around.class, AspectJAnnotationType.AtAround);
            annotationTypeMap.put(Before.class, AspectJAnnotationType.AtBefore);
            annotationTypeMap.put(After.class, AspectJAnnotationType.AtAfter);
            annotationTypeMap.put(AfterReturning.class, AspectJAnnotationType.AtAfterReturning);
            annotationTypeMap.put(AfterThrowing.class, AspectJAnnotationType.AtAfterThrowing);
        }

        private final A annotation;

        private final AspectJAnnotationType annotationType;

        public AspectJAnnotation(A annotation) {
            this.annotation = annotation;
            this.annotationType = determineAnnotationType(annotation);
        }

        public static boolean containsAspectJAnnotation(Annotation annotation) {
            return annotationTypeMap.containsKey(annotation.annotationType());
        }

        private AspectJAnnotationType determineAnnotationType(A annotation) {
            AspectJAnnotationType type = annotationTypeMap.get(annotation.annotationType());
            if (type != null) {
                return type;
            }
            throw new IllegalStateException("Unknown annotation type: " + annotation);
        }

        public AspectJAnnotationType getAnnotationType() {
            return this.annotationType;
        }

        public A getAnnotation() {
            return this.annotation;
        }

        public String getAnnotationValue() {
            switch (this.annotationType) {
                case AtPointcut:
                    return ((org.aspectj.lang.annotation.Pointcut) annotation).value();
                case AtAround:
                    return ((Around) annotation).value();
                case AtBefore:
                    return ((Before) annotation).value();
                case AtAfter:
                    return ((After) annotation).value();
                case AtAfterReturning:
                    return ((AfterReturning) annotation).value();
                case AtAfterThrowing:
                    return ((AfterThrowing) annotation).value();
                default:
                    return null;
            }
        }
    }
}
