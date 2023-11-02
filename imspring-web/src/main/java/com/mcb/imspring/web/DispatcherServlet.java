package com.mcb.imspring.web;

import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.core.collections.Ordered;
import com.mcb.imspring.web.exception.ServerErrorException;
import com.mcb.imspring.web.handler.HandlerExecutionChain;
import com.mcb.imspring.web.handler.HandlerMapping;
import com.mcb.imspring.web.servlet.FrameworkServlet;
import com.mcb.imspring.web.utils.WebUtils;
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
        HandlerExecutionChain chain = this.getHandler(request);
        if (chain == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
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
}
