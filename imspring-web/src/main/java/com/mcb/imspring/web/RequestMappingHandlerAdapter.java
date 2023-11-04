package com.mcb.imspring.web;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.collections.Ordered;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.web.exception.ServerErrorException;
import com.mcb.imspring.web.handler.HandlerAdapter;
import com.mcb.imspring.web.handler.HandlerMethod;
import com.mcb.imspring.web.handler.HandlerMethodReturnValueHandler;
import com.mcb.imspring.web.handler.MethodParameter;
import com.mcb.imspring.web.request.ServletWebRequest;
import com.mcb.imspring.web.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 真正处理 Controller 逻辑，获取请求参数，通过反射调用 HandlerMethod 处理逻辑，然后再封装返回结果进行返回
 */
@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter, InitializingBean, Ordered {

    private List<HandlerMethodReturnValueHandler> returnValueHandlers;

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleInternal(request, response, (HandlerMethod) handler);
    }

    @Override
    public void afterPropertiesSet() {
        if (this.returnValueHandlers == null) {
            this.returnValueHandlers = new ArrayList<>();
            returnValueHandlers.add(new RequestResponseBodyMethodProcessor());
        }
    }

    private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        // 1、获取参数
        Object[] args = getMethodArgumentValues(request, response, handler);
        // 2、调用 controller 执行获取返回值
        Object returnValue = handler.getMethod().invoke(handler.getBean(), args);
        // 3、处理返回值
        ModelAndView mav = new ModelAndView();
        MethodParameter returntype = new MethodParameter(handler.getMethod(), returnValue.getClass());
        HandlerMethodReturnValueHandler returnValueHandler = getReturnValueHandler(returntype);
        if (returnValueHandler != null) {
            ServletWebRequest webRequest = new ServletWebRequest(request, response);
            returnValueHandler.handleReturnValue(returnValue, returntype, mav, webRequest);
            return mav;
        }
        throw new ServerErrorException(String.format("No return value handlers for %s", returnValue.getClass()));
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

    private HandlerMethodReturnValueHandler getReturnValueHandler(MethodParameter returnType) {
        for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
            if (handler.supportsReturnType(returnType)) {
                return handler;
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
