package com.mcb.imspring.core;

import com.mcb.imspring.core.io.AnnotatedBeanDefinitionReader;
import com.mcb.imspring.core.io.ClassPathBeanDefinitionScanner;
import com.mcb.imspring.core.utils.Assert;

/**
 * ApplicationContext实现类，这里使用装饰器模式，对beanFactory进行了一层封装
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    /**
     * 读取配置类的信息，包括一些内部后置处理器，将这些类解析成BeanDefinition，注册到Spring容器中
     */
    private final AnnotatedBeanDefinitionReader reader;

    /**
     * 扫描指定路径下的@Component类，将这些类解析成BeanDefinition，注册到Spring容器中
     */
    private final ClassPathBeanDefinitionScanner scanner;

    /**
     * 构造DefaultListableBeanFactory、AnnotatedBeanDefinitionReader、ClassPathBeanDefinitionScanner
     * 其中DefaultListableBeanFactory在父类里面构造
     */
    public AnnotationConfigApplicationContext() {
        this.reader = new AnnotatedBeanDefinitionReader(this);
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    public AnnotationConfigApplicationContext(String scanPackage) {
        this();
        scan(scanPackage);
        refresh();
    }

    public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
        this();
        register(componentClasses);
        refresh();
    }

    /**
     * 扫描配置类，注册为BeanDefinition
     * @param componentClasses
     */
    @Override
    public void register(Class<?>... componentClasses) {
        Assert.notEmpty(componentClasses, "At least one component class must be specified");
        this.reader.register(componentClasses);
    }

    /**
     * 扫描类路径下的文件，注册为BeanDefinition
     * @param basePackages
     */
    @Override
    public void scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        this.scanner.scan(basePackages);
    }

}
