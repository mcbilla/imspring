package com.mcb.imspring.web.handler;

import com.mcb.imspring.core.utils.ResourceUtils;
import com.mcb.imspring.web.annotation.RequestMapping;
import com.mcb.imspring.web.utils.WebUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

/**
 * 保存 url 和 method 的关系，当一个请求到达时，DispatcherServlet 匹配一个 HandlerMethod 并最终调用它处理该请求。
 * Spring 把请求条件保存在 RequestMappingInfo，把控制器方法 保存在 HandlerMethod，这里统一保存在 HandlerMethod。
 */
public class HandlerMethod {
    private final Pattern pattern;

    private final Method method;

    private final Object bean;

    private final String beanName;

    private final Class<?> beanType;

    private final MethodParameter[] parameters;

    public HandlerMethod(String beanName, Method method, Object bean) {
        this.beanName = beanName;
        this.method = method;
        this.bean = bean;
        this.beanType = bean.getClass();
        this.pattern = this.initPattern(this.method, this.beanType);
        this.parameters = this.initParameters(this.method);
    }

    private Pattern initPattern(Method method, Class<?> beanType) {
        StringBuilder sb = new StringBuilder();
        String methodUrl = WebUtils.getRequestMappingPattern(method);
        methodUrl = ResourceUtils.removeBothSlash(methodUrl);
        if (beanType.isAnnotationPresent(RequestMapping.class)) {
            String classUrl = beanType.getAnnotation(RequestMapping.class).value();
            classUrl = ResourceUtils.removeBothSlash(classUrl);
            sb.append(classUrl).append("/");
        }
        sb.append(methodUrl);
        return Pattern.compile(sb.toString());
    }

    private MethodParameter[] initParameters(Method method) {
        Parameter[] params = method.getParameters();
        MethodParameter[] result = new MethodParameter[params.length];
        if (params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                result[i] = new MethodParameter(params[i], i);
            }
        }
        return result;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public MethodParameter[] getParameters() {
        return parameters;
    }
}
