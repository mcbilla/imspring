package com.mcb.imspring.web;

import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.web.handler.HandlerMapping;
import com.mcb.imspring.web.servlet.FrameworkServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    }
}
