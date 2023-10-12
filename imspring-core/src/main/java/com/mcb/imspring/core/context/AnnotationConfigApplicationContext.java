package com.mcb.imspring.core.context;

/**
 * ApplicationContext实现类，这里使用装饰器模式，对beanFactory进行了一层封装
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    private BeanFactory beanFactory;

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        this.beanFactory = new DefaultBeanFactory(configClass);
    }

    @Override
    public BeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }
}
