package com.mcb.imspring.web.handler;

import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.core.context.ApplicationContextAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.core.utils.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

public class AbstractHandlerMapping implements HandlerMapping, InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        initHandlerMethods();
    }

    private void initHandlerMethods() {
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            detectHandlerMethods(beanName);
        }
    }

    private void detectHandlerMethods(String beanName) {
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        return null;
    }
}
