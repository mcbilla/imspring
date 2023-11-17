package com.mcb.imspring.core;

import com.mcb.imspring.core.common.OrderComparator;
import com.mcb.imspring.core.common.Ordered;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Spring IOC容器的默认实现类
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 传说中的IOC容器
     */
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);

    protected List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    public DefaultListableBeanFactory() {
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws BeansException {
        this.beanDefinitionMap.remove(beanName);
        this.beanDefinitionNames.remove(beanName);
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions(Class<?> type) {
        return this.beanDefinitionMap.values().stream()
                .filter(def -> def.getTargetType() != null && type.isAssignableFrom(def.getTargetType()))
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
        // 按照 order 升序排序
        List<BeanDefinition> candidateDefs = new ArrayList<>(beanDefinitionMap.values());
        candidateDefs.sort((bd1, bd2) -> {
            int i1 = OrderComparator.getOrder(bd1.getTargetType());
            int i2 = OrderComparator.getOrder(bd2.getTargetType());
            return Integer.compare(i1, i2);
        });
        for (BeanDefinition def : candidateDefs) {
            this.getBean(def.getName());
        }
        logger.debug("BeanFactory pre instantiate finish，all beans: {}", beanDefinitionMap.keySet());
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }
}
