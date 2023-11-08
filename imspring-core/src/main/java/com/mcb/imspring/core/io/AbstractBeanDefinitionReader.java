package com.mcb.imspring.core.io;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionReader;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final BeanDefinitionRegistry registry;

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    protected BeanDefinition createBeanDefinition(Class<?> clazz, String beanName) {
        if (this.registry.containsBeanDefinition(beanName)) {
            throw new BeansException("Duplicate bean name: " + beanName);
        }
        logger.debug("create BeanDefinition beanName: [{}]，class: [{}]", beanName, clazz.getName());
        Constructor constructor = BeanUtils.getBeanConstructor(clazz);
        return new BeanDefinition(beanName, clazz, constructor);
    }
}
