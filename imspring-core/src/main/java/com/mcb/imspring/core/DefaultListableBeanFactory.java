package com.mcb.imspring.core;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.io.AnnotatedBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Spring IOC容器的默认实现类
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 传说中的ioc容器
     */
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    protected List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    protected AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader();

    /**
     * 只做ioc容器的初始化，并没有实例化bean，在真正调用getBean的时候再进行实例化
     * @param configClass
     */
    public DefaultListableBeanFactory(Class<?> configClass) {
        try {
            reader.loadBeanDefinitions(configClass);
        } catch (IOException e) {
            throw new BeansException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        registerBeanDefinition();
        registerBeanPostProcessor();
        logger.debug("BeanFactory init finish");
    }

    /**
     * 注册BeanDefinition，这时候BeanDefinition还没有实例化
     */
    private void registerBeanDefinition() {
        for (Map.Entry<String, BeanDefinition> entry : reader.getRegistry().entrySet()) {
            String name = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            beanDefinitionMap.put(name, beanDefinition);
        }
    }

    /**
     * 注册BeanPostProcessor，这时候BeanPostProcessor已经被实例化
     */
    private void registerBeanPostProcessor() {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                addBeanPostProcessor(getBean(beanDefinition.getName()));
            }
        }
    }

    @Override
    protected boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions(Class<?> type) {
        return this.beanDefinitionMap.values().stream()
                .filter(def -> type.isAssignableFrom(def.getBeanClass()))
                .sorted().collect(Collectors.toList());
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> type) {
        List<BeanDefinition> defs = getBeanDefinitions(type);
        if (defs.isEmpty()) {
            throw new BeansException(String.format("no such bean with type '%s' found", type.getName()));
        }
        if (defs.size() > 1) {
            throw new BeansException(String.format("Multiple bean with type '%s' found", type.getName()));
        }
        return defs.get(0);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name, Class<?> requiredType) {
        BeanDefinition def = null;
        if (name != null && requiredType == null) {
            def = getBeanDefinition(name);
        } else if (name == null && requiredType != null) {
            def = getBeanDefinition(requiredType);
        } else if (name != null && requiredType != null) {
            BeanDefinition var = getBeanDefinition(name);
            if (requiredType.isAssignableFrom(var.getBeanClass())) {
                def = var;
            }
        }
        return def;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public void preInstantiateSingletons() {
        for (String beanName : beanDefinitionMap.keySet()) {
            this.getBean(beanName);
        }
        logger.debug("pre init instance finish [{}]", beanDefinitionMap.keySet());
    }
}
