package com.mcb.imspring.core;

import com.mcb.imspring.core.collections.Ordered;
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
        // 按照order升序排序
        Comparator<BeanDefinition> c = (o1, o2) -> getBeanOrder(o1) - getBeanOrder(o2);
        Queue<BeanDefinition> queue = new PriorityQueue<>(c);
        queue.addAll(beanDefinitionMap.values());
        for (BeanDefinition def : queue) {
            this.getBean(def.getName());
        }
        logger.debug("beanFactory pre init finish, all beans: {}", beanDefinitionMap.keySet());
    }

    private int getBeanOrder(BeanDefinition def) {
        int order = Ordered.DEFAULT_PRECEDENCE;
        if (Ordered.class.isAssignableFrom(def.getBeanClass())) {
            try {
                Method method = def.getBeanClass().getMethod("getOrder", null);
                order = (int) method.invoke(def.getBeanClass().newInstance(), null);
            } catch (Exception e) {
                throw new BeansException(e);
            }
        }
        return order;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }
}
