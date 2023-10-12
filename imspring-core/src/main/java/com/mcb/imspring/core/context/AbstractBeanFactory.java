package com.mcb.imspring.core.context;

import com.mcb.imspring.core.exception.BeanNotOfRequiredTypeException;
import com.mcb.imspring.core.exception.NoSuchBeanDefinitionException;
import com.mcb.imspring.core.exception.NoUniqueBeanDefinitionException;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring IOC容器的的抽象类，实现ConfigurableBeanFactory的接口
 * Spring的单例IOC容器实际上由SingletonBeanRegistry管理的，默认实现类是DefaultSingletonBeanRegistry
 * 这里为了简化直接把IOC容器放在AbstractBeanFactory中
 */
public abstract class AbstractBeanFactory implements ConfigurableBeanFactory{
    /**
     * 传说中的ioc容器
     */
    protected Map<String, BeanDefinition> ioc;

    /**
     * 检测是否存在指定Name的Bean
     */
    @Override
    public boolean containsBean(String name) {
        return this.ioc.containsKey(name);
    }

    /**
     * 通过Name查找Bean，不存在时抛出NoSuchBeanDefinitionException
     */
    @Override
    public <T> T getBean(String name) {
        BeanDefinition def = this.ioc.get(name);
        if (def == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s'.", name));
        }
        return (T) def.getInstance();
    }

    /**
     * 通过Name和Type查找Bean，不存在抛出NoSuchBeanDefinitionException，存在但与Type不匹配抛出BeanNotOfRequiredTypeException
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        T t = findBean(name, requiredType);
        if (t == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s' and type '%s'.", name, requiredType));
        }
        return t;
    }

    /**
     * 通过Type查找Beans
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(requiredType);
        if (def == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with type '%s'.", requiredType));
        }
        return (T) def.getInstance();
    }

    /**
     * 通过Type查找Bean，不存在抛出NoSuchBeanDefinitionException，存在多个但缺少唯一@Primary标注抛出NoUniqueBeanDefinitionException
     */
    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        List<BeanDefinition> defs = findBeanDefinitions(requiredType);
        if (defs.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(defs.size());
        for (BeanDefinition def : defs) {
            list.add((T) def.getInstance());
        }
        return list;
    }

    /**
     * findXxx与getXxx类似，但不存在时返回null
     */
    @Nullable
    protected <T> T findBean(String name, Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(name, requiredType);
        if (def == null) {
            return null;
        }
        return (T) def.getInstance();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T findBean(Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(requiredType);
        if (def == null) {
            return null;
        }
        return (T) def.getInstance();
    }

    /**
     * 根据Type查找若干个BeanDefinition，返回0个或多个。
     */
    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return this.ioc.values().stream()
                .filter(def -> type.isAssignableFrom(def.getBeanClass()))
                .sorted().collect(Collectors.toList());
    }

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> defs = findBeanDefinitions(type);
        if (defs.isEmpty()) {
            throw new NoSuchBeanDefinitionException(String.format("no such bean with type '%s' found", type.getName()));
        }
        if (defs.size() > 1) {
            throw new NoUniqueBeanDefinitionException(String.format("Multiple bean with type '%s' found", type.getName()));
        }
        return defs.get(0);
    }

    /**
     * 根据Name查找BeanDefinition，如果Name不存在，返回null
     */
    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return this.ioc.get(name);
    }

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        BeanDefinition def = findBeanDefinition(name);
        if (def == null) {
            return null;
        }
        if (!requiredType.isAssignableFrom(def.getBeanClass())) {
            throw new BeanNotOfRequiredTypeException(String.format("Autowire required type '%s' but bean '%s' has actual type '%s'.", requiredType.getName(),
                    name, def.getBeanClass().getName()));
        }
        return def;
    }
}
