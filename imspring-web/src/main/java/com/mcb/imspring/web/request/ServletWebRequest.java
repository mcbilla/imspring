package com.mcb.imspring.web.request;

import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletWebRequest implements NativeWebRequest{

    private final HttpServletRequest request;

    @Nullable
    private HttpServletResponse response;

    public ServletWebRequest(HttpServletRequest request) {
        this.request = request;
    }

    public ServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public Object getNativeRequest() {
        return null;
    }

    @Override
    public Object getNativeResponse() {
        return null;
    }
}
