package com.mcb.imspring.core;

import com.mcb.imspring.core.common.OrderComparator;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.context.ObjectFactory;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private volatile Set<String> beanDefinitionNames = new LinkedHashSet<>(256);

    // beanPostProcessors列表，每个beanPostProcessor会对所有bean实例生效
    protected final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    // 一级缓存，存放完全初始化好的 bean，从该缓存中取出的 bean 可以直接使用
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // 二级缓存，提前曝光的单例对象的cache，存放原始的 bean 对象（尚未填充属性），用于解决循环依赖
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    // 三级缓存，存放 bean 工厂对象，这个对象其实是一个函数式接口，接口实现是创建一个 bean 对象，用来解决 AOP 的循环依赖
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

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

    @Override
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    /**
     * 获取实例，这里是Spring 解决循环依赖的关键：
     * Spring 在创建 bean 的时候并不是等它完全完成，而是在创建过程中将创建中的 bean 的 ObjectFactory 提前曝光（即加入到 singletonFactories 缓存中）
     * 一旦下一个 bean 创建的时候需要依赖 bean ，则直接使用 ObjectFactory 的 getObject() 获取。
     */
    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    /**
     * 获取bean实例
     * 1、先从一级缓存拿
     * 2、一级缓存拿不到，再从二级缓存拿
     * 3、二级缓存拿不到，从三级缓存拿，拿到之后放入二级缓存，并删除三级缓存
     * @param beanName
     * @param allowEarlyReference 是否获取提前实例化的bean
     * @return
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
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

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    @Override
    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeansException(String.format("bean %s currently in creation before handle fail", beanName));
        }
    }

    @Override
    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new BeansException(String.format("bean %s currently in creation after handle fail", beanName));
        }
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }
}
