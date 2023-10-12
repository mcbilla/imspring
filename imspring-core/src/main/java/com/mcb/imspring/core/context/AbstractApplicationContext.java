package com.mcb.imspring.core.context;

import java.util.List;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext{

    @Override
    public abstract BeanFactory getBeanFactory() throws IllegalStateException;

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public <T> T getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        return getBeans(requiredType);
    }

    @Override
    public void close() {
        System.out.println("自动关闭");
    }
}
