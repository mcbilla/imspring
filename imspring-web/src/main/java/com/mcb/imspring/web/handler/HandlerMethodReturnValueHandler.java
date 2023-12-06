package com.mcb.imspring.web.handler;

import com.mcb.imspring.web.request.NativeWebRequest;
import com.mcb.imspring.web.mav.ModelAndView;
import com.sun.istack.internal.Nullable;

import java.io.IOException;

/**
 * 用于处理返回值，目前只能处理字符串返回值，也就是带有@ResponseBody的方法或者带有@RestController的类
 */
public interface HandlerMethodReturnValueHandler {

    boolean supportsReturnType(MethodParameter returnType);

    void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndView mav, NativeWebRequest webRequest) throws IOException;
}
