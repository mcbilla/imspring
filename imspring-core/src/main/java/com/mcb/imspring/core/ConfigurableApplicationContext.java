package com.mcb.imspring.core;

import com.mcb.imspring.core.context.BeanFactoryPostProcessor;

public interface ConfigurableApplicationContext extends ApplicationContext,  AutoCloseable {
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void register(Class<?>... componentClasses);

    void scan(String... basePackages);
}
