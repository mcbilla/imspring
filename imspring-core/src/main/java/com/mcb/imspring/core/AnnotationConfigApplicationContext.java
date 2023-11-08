package com.mcb.imspring.core;

import com.mcb.imspring.core.io.AnnotatedBeanDefinitionReader;

/**
 * ApplicationContext实现类，这里使用装饰器模式，对beanFactory进行了一层封装
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    private final AnnotatedBeanDefinitionReader reader;

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        super(configClass);
        this.reader = new AnnotatedBeanDefinitionReader(this.beanFactory);
        refresh();
    }
}
