package com.mcb.imspring.web;

import com.mcb.imspring.core.collections.Ordered;
import com.mcb.imspring.web.exception.ServerErrorException;
import com.mcb.imspring.web.handler.HandlerAdapter;
import com.mcb.imspring.web.handler.HandlerMethod;
import com.mcb.imspring.web.handler.MethodParameter;
import com.mcb.imspring.web.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RequestMappingHandlerAdapter implements HandlerAdapter, Ordered {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleInternal(request, response, (HandlerMethod) handler);
    }

    private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        Object[] args = getMethodArgumentValues(request, response, handler);
        Object returnValue = handler.getMethod().invoke(handler.getBean(), args);
        return null;
    }

    private Object[] getMethodArgumentValues(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        // HandlerMethod保存的参数列表
        MethodParameter[] paramTypes = handler.getParameters();
        Map<String, MethodParameter> map = Arrays.stream(paramTypes).collect(Collectors.toMap(MethodParameter::getParameterName, Function.identity()));
        Object[] paramValues = new Object[paramTypes.length];
        // 前端传的参数列表，不包含HttpServletRequest和HttpServletResponse
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            if (!map.containsKey(param.getKey())) {
                continue;
            }
            // 参数值，全都是字符串形式
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            MethodParameter mp = map.get(param.getKey());
            // 参数下标
            int index = mp.getParameterIndex();
            // 参数类型
            Class<?> type = mp.getParameterType();
            paramValues[index] = convert(type, value);
        }
        // 处理参数列表的HttpServletRequest和HttpServletResponse
        if (map.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = map.get(HttpServletRequest.class.getName()).getParameterIndex();
            paramValues[reqIndex] = request;
        }
        if (map.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = map.get(HttpServletResponse.class.getName()).getParameterIndex();
            paramValues[respIndex] = response;
        }
        return paramValues;
    }

    /**
     * url传过来的参数都是String类型，需要转成特定类型
     */
    private Object convert(Class<?> type, String value) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new ServerErrorException(String.format("can not cast %s to %s", value.getClass(), type));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
