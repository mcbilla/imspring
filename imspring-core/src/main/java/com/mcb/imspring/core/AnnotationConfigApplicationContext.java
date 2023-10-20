package com.mcb.imspring.core;

/**
 * ApplicationContext实现类，这里使用装饰器模式，对beanFactory进行了一层封装
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        super(configClass);
        refresh();
    }
}
