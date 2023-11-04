package com.mcb.imspring.web.handler;

import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MethodParameter {

    private final Executable executable;

    private final Parameter parameter;

    private final int parameterIndex;

    private Class<?> parameterType;

    private String parameterName;

    private volatile Class<?> containingClass;

    private Class<?> returnValueType;

    public MethodParameter(Method method, @Nullable Parameter parameter, int parameterIndex) {
        this.executable = method;
        this.containingClass = method.getDeclaringClass();
        this.parameter = parameter;
        this.parameterIndex = parameterIndex;
        if (parameter != null) {
            this.parameterType = parameter.getType();
            this.parameterName = parameter.getName();
        }
    }

    public MethodParameter(Method method, Class<?> returnValueType) {
        this(method, null, -1);
        this.returnValueType = returnValueType;
    }

    public Executable getExecutable() {
        return executable;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Class<?> getContainingClass() {
        return containingClass;
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return this.executable.isAnnotationPresent(annotationType);
    }
}
