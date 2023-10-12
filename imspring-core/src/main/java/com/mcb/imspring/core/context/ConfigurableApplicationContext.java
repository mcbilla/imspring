package com.mcb.imspring.core.context;

/**
 * Used for BeanPostProcessor.
 */
public interface ConfigurableApplicationContext extends ApplicationContext, AutoCloseable{
    /**
     * 关闭并执行所有bean的destroy方法
     */
    @Override
    void close();
}
