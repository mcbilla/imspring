package com.mcb.imspring.web.handler;

import java.lang.reflect.Parameter;

public class MethodParameter {

    private final Parameter parameter;

    private final int parameterIndex;

    private final Class<?> parameterType;

    private final String parameterName;

    public MethodParameter(Parameter parameter, int parameterIndex) {
        this.parameter = parameter;
        this.parameterIndex = parameterIndex;
        this.parameterType = parameter.getType();
        this.parameterName = parameter.getName();
    }
}
