package com.mcb.imspring.core.io;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionReader;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final BeanDefinitionRegistry registry;

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    protected BeanDefinition createBeanDefinition(String beanName) {
        return this.createBeanDefinition(beanName, null);
    }

    protected BeanDefinition createBeanDefinition(String beanName, Class<?> beanClass) {
        // 允许BeanDefinition同名覆盖
        if (this.registry.containsBeanDefinition(beanName)) {
            logger.debug("overwrite duplicate BeanDefinition，beanName: [{}]" + beanName);
        } else  {
            logger.debug("create BeanDefinition beanName: [{}]", beanName);
        }
        if (beanClass == null) {
            return new BeanDefinition(beanName);
        } else {
            BeanDefinition bd = new BeanDefinition(beanName, beanClass);
            Method[] methods = beanClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    bd.setInitMethodName(method.getName());
                }

                if (method.isAnnotationPresent(PreDestroy.class)) {
                    bd.setDestroyMethodName(method.getName());
                }
            }
            bd.setTargetType(beanClass);
            return bd;
        }
    }
}
