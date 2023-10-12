package com.mcb.imspring.core.context;

public interface ApplicationContext extends BeanFactory {
    BeanFactory getBeanFactory() throws IllegalStateException;
}
