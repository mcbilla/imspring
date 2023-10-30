package com.mcb.imspring.web.handler;

import javax.servlet.http.HttpServletRequest;

public interface HandlerMapping {
    HandlerExecutionChain getHandler(HttpServletRequest request);
}
