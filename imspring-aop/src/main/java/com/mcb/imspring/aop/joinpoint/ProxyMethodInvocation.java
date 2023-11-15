package com.mcb.imspring.aop.joinpoint;

import org.aopalliance.intercept.MethodInvocation;

public interface ProxyMethodInvocation extends MethodInvocation {
    Object getProxy();

    void setArguments(Object... arguments);

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
