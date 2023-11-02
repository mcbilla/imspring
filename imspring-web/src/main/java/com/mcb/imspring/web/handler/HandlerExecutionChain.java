package com.mcb.imspring.web.handler;

import com.mcb.imspring.web.interceptor.HandlerInterceptor;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * HandlerExecutionChain 就是一个 Handler 和一组特定顺序的 HandlerInterceptor。
 */
public class HandlerExecutionChain {
    private final Object handler;

    private final List<HandlerInterceptor> interceptorList = new ArrayList<>();

    public HandlerExecutionChain(Object handler) {
        this(handler, (HandlerInterceptor[]) null);
    }

    public HandlerExecutionChain(Object handler, @Nullable HandlerInterceptor... interceptors) {
        this(handler, (interceptors != null ? Arrays.asList(interceptors) : Collections.emptyList()));
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList) {
        this.handler = handler;
        this.interceptorList.addAll(interceptorList);
    }

    public Object getHandler() {
        return handler;
    }

    public List<HandlerInterceptor> getInterceptorList() {
        return interceptorList;
    }
}
