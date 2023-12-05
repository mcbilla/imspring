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
import com.mcb.imspring.web.listener.SpringServletContextListener;
import com.mcb.imspring.web.handler.HandlerMethod;
import com.mcb.imspring.web.handler.HandlerInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Dispatcher 是 Spring MVC 的核心类，主要流程：
 * 1、在 WEB-INF/web.xml 中配置好 {@link DispatcherServlet} 和 {@link SpringServletContextListener}
 * 2、tomcat 启动，{@link SpringServletContextListener} 优先于所有 servlet 运行，获取 ApplicationContext，并加载到 ServletContext。
 * 3、tomcat 加载 {@link DispatcherServlet}，从 ServletContext 获取 ApplicationContext，然后从 ApplicationContext 中获取 {@link HandlerMapping} 和 {@link HandlerAdapter} 的 bean 实例封装到自身。
 * 4、请求进来，从 {@link HandlerMapping} 中获取和 url 适配的 {@link HandlerExecutionChain}，里面包含了一个 {@link HandlerMethod} 和一系列的 {@link HandlerInterceptor}
 * 5、处理 {@link HandlerExecutionChain} ，包括执行 {@link HandlerMethod} 的业务逻辑（Conteroller的业务逻辑）和依次调用所有 {@link HandlerInterceptor} 的 preHandler、postHandler 和 afterCompletion 方法。
 * 6、在执行 {@link HandlerMethod} 的时候，封装 {@link HandlerAdapter} 真正去处理业务逻辑，并返回处理结果。
 */
public class DispatcherServlet extends FrameworkServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<HandlerMapping> handlerMappings;

    private List<HandlerAdapter> handlerAdapters;

    @Override
    protected void onRefresh(ApplicationContext context) {
        initHandlerMappings(context);
        initHandlerAdapters(context);
        logger.info("DispatchServlet is init.");
    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = context.getBeans(HandlerMapping.class);
    }

    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = context.getBeans(HandlerAdapter.class);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) {
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

            // 4、HandlerAdapter 执行 Controller 业务逻辑
            mv = ha.handle(request, response, chain.getHandler());

            // 5、HandlerInterceptor postHandle 处理
            chain.applyPostHandle(request, response, mv);

            // 6、页面渲染
            processDispatchResult(request, response, chain, mv);
        } catch (Exception ex) {
            logger.error("server error", ex);
            dispatchException = ex;
        } finally {
            // 7、HandlerInterceptor afterCompletion 处理
            if (chain != null) {
                chain.triggerAfterCompletion(request, response, dispatchException);
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

    private HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain chain, ModelAndView mv) throws Exception {
        // 如果mv还没有被进行处理，就进行页面渲染
        // 这里只支持json数据，且已经把json数据添加到response了，没有需要进行处理的mv了
        if (!mv.isRequestHandled()) {

        }
    }
}
