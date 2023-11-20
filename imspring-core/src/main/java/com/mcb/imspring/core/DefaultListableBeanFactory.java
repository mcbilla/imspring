package com.mcb.imspring.core;

import com.mcb.imspring.core.common.OrderComparator;
import com.mcb.imspring.core.common.Ordered;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.Assert;
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
 * Spring 的单例实例容器实际上由 SingletonBeanRegistry 管理的，默认实现类是 DefaultSingletonBeanRegistry，这里为了简化直接把实例容器也放在 BeanFactory 中统一管理
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // BeanDefinition的集合，key是beanName，value是bean的类信息，但不包括bean实例。下面集合的key都是beanName
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    // beanName列表，和beanDefinitionMap对应
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);

    // beanPostProcessors列表，每个beanPostProcessor会对所有bean实例生效
    protected final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    // 一级缓存，存放完全初始化好的 bean，从该缓存中取出的 bean 可以直接使用
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // 二级缓存，提前曝光的单例对象的cache，存放原始的 bean 对象（尚未填充属性），用于解决循环依赖
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    // 三级缓存，存放 bean 工厂对象，用来解决 AOP 的循环依赖，暂时没用上
//    private final Map<String, Object> singletonFactories = new HashMap<>(16);

    // 已经实例化结束的beanName集合
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    // 正在实例化的beanName集合
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

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
            if (requiredType.isAssignableFrom(var.getTargetType())) {
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
        logger.debug("BeanFactory pre instantiate finish，all beans: {}", registeredSingletons);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(singletonObject, "Singleton object must not be null");
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        // TODO 这里是解决循环依赖的关键
        return this.singletonObjects.get(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        synchronized (this.singletonObjects) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }
}
