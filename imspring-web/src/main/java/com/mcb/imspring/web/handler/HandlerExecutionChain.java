package com.mcb.imspring.web.handler;

import com.mcb.imspring.core.utils.CollectionUtils;
import com.mcb.imspring.web.view.ModelAndView;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * HandlerExecutionChain 就是一个 HandlerMethod 和一组特定顺序的 HandlerInterceptor。
 */
public class HandlerExecutionChain {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Object handler;

    private final List<HandlerInterceptor> interceptorList = new ArrayList<>();

    private int interceptorIndex = -1;

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

    public void addInterceptor(HandlerInterceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    public void addInterceptor(int index, HandlerInterceptor interceptor) {
        this.interceptorList.add(index, interceptor);
    }

    public void addInterceptors(HandlerInterceptor... interceptors) {
        CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
    }

    /**
     * HandlerInterceptor preHandle 处理
     */
    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) {
        for (int i = 0; i < this.interceptorList.size(); i++) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            if (!interceptor.preHandle(request, response, this.handler)) {
                triggerAfterCompletion(request, response, null);
                return false;
            }
            this.interceptorIndex = i;
        }
        return true;
    }

    /**
     * HandlerInterceptor postHandle 处理
     */
    public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv) {
        for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            interceptor.postHandle(request, response, this.handler, mv);
        }
    }

    /**
     * HandlerInterceptor afterCompletion 处理
     */
    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            try {
                interceptor.afterCompletion(request, response, this.handler, ex);
            }
            catch (Throwable ex2) {
                logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
            }
        }
    }

    public Object getHandler() {
        return handler;
    }

    public List<HandlerInterceptor> getInterceptorList() {
        return interceptorList;
    }
}
