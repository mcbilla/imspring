package com.mcb.imspring.web;

import com.mcb.imspring.core.annotation.Controller;
import com.mcb.imspring.web.annotation.RequestMapping;
import com.mcb.imspring.web.handler.HandlerMapping;
import com.mcb.imspring.web.servlet.FrameworkServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatchServlet extends FrameworkServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private List<HandlerMapping> handlerMappings;

    @Override
    public void init(ServletConfig config) throws ServletException {

        logger.info("Spring framework is init.");
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
