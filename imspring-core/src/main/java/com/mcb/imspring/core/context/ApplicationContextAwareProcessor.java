package com.mcb.imspring.core.context;

import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.core.exception.BeansException;

public class ApplicationContextAwareProcessor implements BeanPostProcessor{

    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
        }
        return bean;
    }
}
