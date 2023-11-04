package com.mcb.imspring.web.request;

public interface NativeWebRequest extends WebRequest {
    Object getNativeRequest();

    Object getNativeResponse();
}
