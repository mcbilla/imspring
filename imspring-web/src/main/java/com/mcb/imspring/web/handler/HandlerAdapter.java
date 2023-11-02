package com.mcb.imspring.web.handler;

import com.mcb.imspring.web.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {

    boolean supports(Object handler);

    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler);
}
