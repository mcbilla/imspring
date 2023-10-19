package com.mcb.imspring.core;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanFactoryAware;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring IOC容器的的抽象类，实现ConfigurableBeanFactory的接口
 * Spring的单例IOC容器实际上由SingletonBeanRegistry管理的，默认实现类是DefaultSingletonBeanRegistry
 * 这里为了简化直接把IOC容器放在AbstractBeanFactory中
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean containsBean(String name) {
        return containsBeanDefinition(name);
    }

    @Override
    public <T> T getBean(String name) {
        return getBean(name, null);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return getBean(null, requiredType);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        BeanDefinition def = getBeanDefinition(name, requiredType);
        if (def == null) {
            throw new BeansException(String.format("No bean defined with name '%s' and type '%s'.", name, requiredType));
        }
        Object instance = def.getInstance();
        if (instance == null) {
            instance = doGetBean(name, requiredType, null);
        }
        return (T) instance;
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        List<BeanDefinition> defs = getBeanDefinitions(requiredType);
        if (defs.isEmpty()) {
            throw new BeansException(String.format("No bean defined with type '%s'.", requiredType));
        }
        List<T> list = new ArrayList<>(defs.size());
        for (BeanDefinition def : defs) {
            list.add(getBean(def.getName()));
        }
        return list;
    }

    public  <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args) {
        BeanDefinition def = getBeanDefinition(name, requiredType);
        // 初始化bean实例
        Object bean = createBean(def, args);

        // 属性填充
        bean = populateBean(bean, name);

        // 初始化bean
        bean = initializeBean(bean, name);

        // 完成bean初始化
        def.setInstance(bean);

        return (T) bean;
    }

    /**
     * 初始化bean实例，暂时只支持构造器创建
     */
    private Object createBean(BeanDefinition def, Object[] args) {
        try {
            Constructor cons = def.getConstructor();
            Object bean = cons.newInstance(args);
            return bean;
        } catch (Exception e) {
            throw new BeansException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
        }
    }

    /**
     * 属性填充，即常说的依赖注入
     */
    private Object populateBean(Object bean, String name) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String propertyBeanName;
            if (autowired.value() != null && autowired.value().length() > 0) {
                propertyBeanName = autowired.value();
            } else {
                propertyBeanName = BeanUtils.getBeanName(field.getType().getSimpleName());
            }
            try {
                field.setAccessible(true);
                field.set(bean, getBean(propertyBeanName));
            } catch (IllegalAccessException e) {
                throw new BeansException(String.format("Exception when autowired '%s': %s", name, field.getName()), e);
            }
        }
        return bean;
    }

    /**
     * 初始化bean，包括BeanPostProcessorq前置处理、init方法、BeanPostProcessor后置处理
     */
    private Object initializeBean(Object bean, String name) {
        // BeanPostProcessor前置处理
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object processed = beanPostProcessor.postProcessBeforeInitialization(bean, name);
            if (processed == null) {
                throw new BeansException(String.format("post processor before handler returns null when process bean '%s' by %s", name, beanPostProcessor.getClass().getName()));
            }
            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
            if (bean != processed) {
                logger.debug("Bean '{}' was replaced by post processor before handler {}.", name, beanPostProcessor.getClass().getName());
                bean = processed;
            }
        }

        // TODO init方法

        // BeanPostProcessor后置处理
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object processed = beanPostProcessor.postProcessAfterInitialization(bean, name);
            if (processed == null) {
                throw new BeansException(String.format("post processor after handler returns null when process bean '%s' by %s", name, beanPostProcessor.getClass().getName()));
            }
            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
            if (bean != processed) {
                logger.debug("Bean '{}' was replaced by post processor after handler {}.", name, beanPostProcessor.getClass().getName());
                bean = processed;
            }
        }
        return bean;
    }

    protected abstract boolean containsBeanDefinition(String beanName);

    /**
     * 根据Name查找BeanDefinition，如果Name不存在，返回null
     */
    @Nullable
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    @Nullable
    protected abstract BeanDefinition getBeanDefinition(Class<?> type);

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    protected abstract BeanDefinition getBeanDefinition(String name, Class<?> requiredType);

    /**
     * 根据Type查找若干个BeanDefinition，返回0个或多个。
     */
    @Nullable
    protected abstract List<BeanDefinition> getBeanDefinitions(Class<?> type);

    protected abstract List<BeanPostProcessor> getBeanPostProcessors();
}
