package com.mcb.imspring.core.context;

import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.exception.BeanCreationException;
import com.mcb.imspring.core.io.ResourceResolver;
import com.mcb.imspring.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext{
    protected final Map<String, BeanDefinition> beans;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        // 1、扫描所有Bean的类
        final Set<String> beanClassNames = scanForClassNames(configClass);

        // 2、初始化扫描到的类，并且将它们放入到ICO容器之中
        this.beans = createBeanDefinitions(beanClassNames);

        // 3、完成依赖注入
    }

    private Set<String> scanForClassNames(Class<?> configClass) {
        // 获取扫描包路径
        ComponentScan scan = BeanUtils.findAnnotation(configClass, ComponentScan.class);
        String scanPackage = scan == null || scan.value().length() == 0 ? configClass.getPackage().getName()  : scan.value();
        logger.debug("component scan in packages: [{}]", scanPackage);

        // 扫描包路径下的所有类
        ResourceResolver rr = new ResourceResolver(scanPackage);
        List<String> classNameList = rr.scan(resource -> {
            String name = resource.getName();
            if (!name.endsWith(".class")) {
                return null;
            }
            return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
        });
        Set<String> classNameSet = new HashSet<>(classNameList);
        if (logger.isDebugEnabled()) {
            classNameSet.forEach((className) -> {
                logger.debug("class found by component scan: {}", className);
            });
        }
        return classNameSet;
    }

    private Map<String, BeanDefinition> createBeanDefinitions(Set<String> classNameSet) {
        Map<String, BeanDefinition> defs = new HashMap<>();
        for (String className : classNameSet) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(e);
            }
            if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface()) {
                continue;
            }
            // TODO

        }
        return defs;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public <T> T getBean(String name) {
        return (T) new Object();
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        return null;
    }

    @Override
    public void close() {
        System.out.println("自动关闭");
    }

    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return null;
    }

    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        return null;
    }

    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return null;
    }

    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        return null;
    }

    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition def) {
        return null;
    }
}
