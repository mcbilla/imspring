package com.mcb.imspring.web.handler;

import com.mcb.imspring.core.utils.ReflectionUtils;
import com.mcb.imspring.web.annotation.ResponseBody;
import com.mcb.imspring.web.annotation.RestController;
import com.mcb.imspring.web.exception.ServerErrorException;
import com.mcb.imspring.web.web.HttpStatus;
import com.mcb.imspring.web.request.NativeWebRequest;
import com.mcb.imspring.web.mav.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RequestResponseBodyMethodProcessor 只能处理带 @RequestBody 的方法或者带 @RestController 的类的返回值
 */
public class RequestResponseBodyMethodProcessor implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return ReflectionUtils.hasAnnotation(returnType.getContainingClass(), RestController.class) ||
                returnType.getExecutable().isAnnotationPresent(ResponseBody.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndView mav, NativeWebRequest webRequest) throws IOException {
        mav.setRequestHandled(true);
        writeWithMessageConverters(returnValue, returnType, webRequest);
    }

    private void writeWithMessageConverters(Object returnValue, MethodParameter returnType, NativeWebRequest webRequest) throws IOException {
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }
        // 目前只支持处理字符串返回值
        if (!(returnValue instanceof CharSequence)) {
            throw new ServerErrorException(String.format("Unsupport handle return type %s", returnValue.getClass()));
        }

        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(returnValue.toString());
    }
}
