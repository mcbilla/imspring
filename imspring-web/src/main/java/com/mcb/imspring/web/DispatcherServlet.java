package com.mcb.imspring.web;

import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.web.exception.ServerErrorException;
import com.mcb.imspring.web.handler.HandlerAdapter;
import com.mcb.imspring.web.handler.HandlerExecutionChain;
import com.mcb.imspring.web.handler.HandlerMapping;
import com.mcb.imspring.web.servlet.FrameworkServlet;
import com.mcb.imspring.web.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class DispatcherServlet extends FrameworkServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<HandlerMapping> handlerMappings;

    @Override
    protected void onRefresh(ApplicationContext context) {
        initHandlerMappings(context);
        logger.info("DispatchServlet is init.");
    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = context.getBeans(HandlerMapping.class);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("请求进来了");
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            throw new ServerErrorException(e);
        }
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HandlerExecutionChain chain = null;
        ModelAndView mv = null;
        Exception dispatchException = null;
        try {
            try {
                // 1、获取 HandlerExecutionChain
                chain = this.getHandler(request);
                if (chain == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                // 2、把 Handler（此时是 HandlerMethod） 封装成 HandlerAdapter
                HandlerAdapter ha = getHandlerAdapter(chain.getHandler());

                // 3、HandlerInterceptor preHandle 处理
                if (!chain.applyPreHandle(request, response)) {
                    return;
                }

                // 4、HandlerAdapter 真正执行 Controller 逻辑
                mv = ha.handle(request, response, chain.getHandler());

                // 5、HandlerInterceptor postHandle 处理
                chain.applyPostHandle(request, response, mv);

            } catch (Exception ex) {
                dispatchException = ex;
            }
            // 6、页面渲染，包括 HandlerInterceptor afterCompletion 处理
            processDispatchResult(request, response, chain, mv, dispatchException);
        } catch (Exception ex) {
            // 7、HandlerInterceptor afterCompletion 处理
            if (chain != null) {
                chain.triggerAfterCompletion(request, response, ex);
            }
        }
    }

    private HandlerExecutionChain getHandler(HttpServletRequest request) {
        if (this.handlerMappings != null) {
            for (HandlerMapping handlerMapping : handlerMappings) {
                HandlerExecutionChain handler = handlerMapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    private HandlerAdapter getHandlerAdapter(Object handler) {
        return null;
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain chain, ModelAndView mv, Exception dispatchException) {
        if (chain != null) {
            chain.triggerAfterCompletion(request, response, null);
        }
    }
}
