package com.mcb.imspring.web.utils;

import com.mcb.imspring.core.annotation.Controller;
import com.mcb.imspring.core.utils.BeanUtils;
import com.mcb.imspring.web.annotation.GetMapping;
import com.mcb.imspring.web.annotation.PostMapping;
import com.mcb.imspring.web.annotation.RequestMapping;
import com.mcb.imspring.web.exception.ServerErrorException;

import java.lang.reflect.Method;

public abstract class WebUtils {
    public static boolean isHandler(Class<?> beanType) {
        return (BeanUtils.hasAnnotation(beanType, Controller.class) ||
                BeanUtils.hasAnnotation(beanType, RequestMapping.class));
    }

    public static boolean isRequestMapping(Method method) {
        return (BeanUtils.hasAnnotation(method, RequestMapping.class) ||
                BeanUtils.hasAnnotation(method, GetMapping.class) ||
                BeanUtils.hasAnnotation(method, PostMapping.class));
    }

    public static String getRequestMappingPattern(Method method) {
        if (BeanUtils.hasAnnotation(method, RequestMapping.class)) {
            return method.getAnnotation(RequestMapping.class).value();
        } else if (BeanUtils.hasAnnotation(method, GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).value();
        } else if (BeanUtils.hasAnnotation(method, PostMapping.class)) {
            return method.getAnnotation(PostMapping.class).value();
        } else  {
            throw new ServerErrorException(String.format("requestmapping url can not be null %s", method));
        }
    }
}
