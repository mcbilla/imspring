package com.mcb.imspring.aop.advisor;

import com.mcb.imspring.aop.advice.*;
import com.mcb.imspring.aop.exception.AopConfigException;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.aop.pointcut.Pointcut;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final Advice EMPTY_ADVICE = new Advice() {};

    private final AspectJExpressionPointcut pointcut;

    private final Class<?> declaringClass;

    private final String aspectName;

    private final Method aspectJAdviceMethod;

    protected Advice advice;

    public AspectJExpressionPointcutAdvisor(Method aspectJAdviceMethod) {
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.aspectName = aspectJAdviceMethod.getName();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = instantiatePointcut(this.declaringClass);
        this.advice = instantiateAdvice(this.aspectJAdviceMethod, this.pointcut);
    }

    private AspectJExpressionPointcut instantiatePointcut(Class<?> declaringClass) {
        Method[] declaredMethods = declaringClass.getDeclaredMethods();
        String expresion = null;
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class)) {
                org.aspectj.lang.annotation.Pointcut annotation = method.getAnnotation(org.aspectj.lang.annotation.Pointcut.class);
                expresion = annotation.value();
                break;
            }
        }
        if (expresion == null) {
            throw new AopConfigException(String.format("pointcut expression can not be null %s", declaringClass.getName()));
        }
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expresion);
        return aspectJExpressionPointcut;
    }

    private Advice instantiateAdvice(Method method, AspectJExpressionPointcut pointcut) {
        Advice advice = null;
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (AspectJAnnotation.containsAspectJAnnotation(annotation.getClass())) {
                AspectJAnnotation aspectJAnnotation = new AspectJAnnotation(annotation);
                switch (aspectJAnnotation.getAnnotationType()) {
                    case AtPointcut:
                        logger.debug("Processing pointcut '" + method.getName() + "'");
                        break;
                    case AtAround:
                        advice = new AspectJAroundAdvice(method, pointcut);
                        break;
                    case AtBefore:
                        advice = new AspectJMethodBeforeAdvice(method, pointcut);
                        break;
                    case AtAfter:
                        advice = new AspectJAfterAdvice(method, pointcut);
                        break;
                    case AtAfterReturning:
                        advice = new AspectJAfterReturningAdvice(method, pointcut);
                        break;
                    case AtAfterThrowing:
                        advice = new AspectJAfterThrowingAdvice(method, pointcut);
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "Unsupported advice type on method: " + method);
                }
            }
        }
        return (advice != null ? advice : EMPTY_ADVICE);
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    protected enum AspectJAnnotationType {
        AtPointcut, AtAround, AtBefore, AtAfter, AtAfterReturning, AtAfterThrowing
    }

    protected static class AspectJAnnotation<A extends Annotation> {
        private static final String[] EXPRESSION_ATTRIBUTES = new String[] {"pointcut", "value"};

        private static Map<Class<?>, AspectJAnnotationType> annotationTypeMap = new HashMap<>(8);

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

        public static boolean containsAspectJAnnotation(Class<?> annotation) {
            return annotationTypeMap.containsKey(annotation);
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
    }
}
