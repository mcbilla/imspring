package com.mcb.imspring.web.servlet;

import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.core.context.ApplicationContextAware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class FrameworkServlet extends HttpServlet implements ApplicationContextAware {

    private ApplicationContext webApplicationContext;

    @Override
    public void init() throws ServletException {
        initServletBean();
    }

    private void initServletBean() {
        onRefresh(this.webApplicationContext);
    }

    protected void onRefresh(ApplicationContext context) {
        // For subclasses: do nothing by default.
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.webApplicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doTrace(req, resp);
    }

    protected final void processRequest(HttpServletRequest request, HttpServletResponse response) {
        doService(request, response);
    }

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response);
}
