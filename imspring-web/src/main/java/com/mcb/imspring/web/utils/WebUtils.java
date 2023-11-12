package com.mcb.imspring.web.utils;

import com.mcb.imspring.core.annotation.Controller;
import com.mcb.imspring.core.utils.ReflectionUtils;
import com.mcb.imspring.web.annotation.GetMapping;
import com.mcb.imspring.web.annotation.PostMapping;
import com.mcb.imspring.web.annotation.RequestMapping;
import com.mcb.imspring.web.exception.ServerErrorException;
import com.mcb.imspring.web.handler.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public abstract class WebUtils {
    public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";

    public static boolean isHandler(Class<?> beanType) {
        return (ReflectionUtils.hasAnnotation(beanType, Controller.class) ||
                ReflectionUtils.hasAnnotation(beanType, RequestMapping.class));
    }

    public static boolean isHandlerInterceptor(Class<?> beanType) {
        return HandlerInterceptor.class.isAssignableFrom(beanType);
    }

    public static boolean isRequestMapping(Method method) {
        return (ReflectionUtils.hasAnnotation(method, RequestMapping.class) ||
                ReflectionUtils.hasAnnotation(method, GetMapping.class) ||
                ReflectionUtils.hasAnnotation(method, PostMapping.class));
    }

    public static String getRequestMappingPattern(Method method) {
        if (ReflectionUtils.hasAnnotation(method, RequestMapping.class)) {
            return method.getAnnotation(RequestMapping.class).value();
        } else if (ReflectionUtils.hasAnnotation(method, GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).value();
        } else if (ReflectionUtils.hasAnnotation(method, PostMapping.class)) {
            return method.getAnnotation(PostMapping.class).value();
        } else  {
            throw new ServerErrorException(String.format("requestmapping url can not be null %s", method));
        }
    }

    public static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return uri;
    }
}
