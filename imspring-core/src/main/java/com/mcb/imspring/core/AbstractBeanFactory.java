package com.mcb.imspring.core;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Value;
import com.mcb.imspring.core.context.*;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import com.mcb.imspring.core.utils.StringUtils;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring IOC容器的的抽象类，实现ConfigurableBeanFactory的接口
 * Spring的单例IOC容器实际上由SingletonBeanRegistry管理的，默认实现类是DefaultSingletonBeanRegistry，这里为了简化直接把IOC容器放在BeanFactory中
 *
 * Spring Bean 的生命周期
 * 1、实例化 Instantiation
 * 2、属性赋值 Populate
 * 3、初始化 Initialization
 * 4、销毁 Destruction
 */
public abstract class AbstractBeanFactory implements BeanFactory, BeanDefinitionRegistry {
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
            StringBuilder errMsg = new StringBuilder("No bean defined with ");
            if (name != null) {
                errMsg.append("name ").append(name);
            }
            if (requiredType != null) {
                errMsg.append("type ").append(requiredType);
            }
            throw new BeansException(errMsg.toString());
        }
        Object instance = def.getInstance();
        if (instance == null) {
            try {
                instance = doGetBean(name, requiredType, null);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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

    public  <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args) throws InvocationTargetException, IllegalAccessException {
        BeanDefinition def = getBeanDefinition(name, requiredType);
        name = def.getName();

        // 初始化bean实例
        Object bean = createBean(def);

        // 属性填充
        populateBean(bean, name);

        // 初始化bean
        bean = initializeBean(bean, name, def);

        // 完成bean初始化
        def.setInstance(bean);

        return (T) bean;
    }

    /**
     * 初始化bean实例，暂时只支持构造器创建
     */
    private Object createBean(BeanDefinition def) {
        try {
            Constructor cons = def.getConstructor();
            final Parameter[] parameters = cons.getParameters();
            Object[] args = new Object[parameters.length];
            Object bean = cons.newInstance(args);
            return bean;
        } catch (Exception e) {
            throw new BeansException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
        }
    }

    /**
     * 属性填充，即常说的依赖注入
     */
    private void populateBean(Object bean, String name) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String propertyBeanName = BeanUtils.getBeanName(field.getType().getSimpleName());
                try {
                    field.setAccessible(true);
                    field.set(bean, getBean(propertyBeanName));
                } catch (IllegalAccessException e) {
                    throw new BeansException(String.format("Exception when autowired '%s': %s", name, field.getName()), e);
                }
            }
            if (field.isAnnotationPresent(Value.class)) {
                Value annotation = field.getAnnotation(Value.class);
                try {
                    field.setAccessible(true);
                    field.set(bean, annotation.value());
                } catch (IllegalAccessException e) {
                    throw new BeansException(String.format("Exception when autowired '%s': %s", name, field.getName()), e);
                }
            }
        }
    }

    /**
     * 检查Aware相关接口并设置依赖，Spring中是通过BeanPostProcessor来处理的，比如ApplicationContextAwareProcessor
     * 这里做了简化处理，仅对 BeanFactoryAware 接口实现类提供了支持
     * @param bean
     * @param name
     * @return
     */
    private void invokeAwareInterfaces(Object bean, String name) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
    }

    /**
     * 初始化bean
     * 1、检查Aware相关接口并设置依赖
     * 2、BeanPostProcessorq前置处理
     * 3、InitializingBean
     * 4、init-method
     * 5、BeanPostProcessor后置处理
     */
    private Object initializeBean(Object bean, String beanName, BeanDefinition def) throws InvocationTargetException, IllegalAccessException {
        // 检查Aware相关接口并设置依赖
        invokeAwareInterfaces(bean, beanName);

        // 调用BeanPostProcessor的postProcessBeforeInitialization方法
        bean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 调用初始化方法，先调用bean的InitializingBean接口方法，后调用bean的自定义初始化方法
        invokeInitMethods(beanName, bean, def);

        // 调用BeanPostProcessor的applyBeanPostProcessorsAfterInitialization方法
        bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);

        return bean;
    }

    private Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object processed = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            if (processed == null) {
                throw new BeansException(String.format("post processor before handler returns null when process bean '%s' by %s", beanName, beanPostProcessor.getClass().getName()));
            }
            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
            if (bean != processed) {
                logger.debug("Bean '{}' was replaced by post processor before handler {}.", beanName, beanPostProcessor.getClass().getName());
                bean = processed;
            }
        }
        return bean;
    }

    private void invokeInitMethods(String beanName, Object bean, BeanDefinition def) throws InvocationTargetException, IllegalAccessException {
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        if (!StringUtils.isEmpty(def.getInitMethodName())) {
            Method initMethod = BeanUtils.findMethod(def.getBeanClass(), def.getInitMethodName());
            if (initMethod != null) {
                initMethod.invoke(bean, null);
            }
        }
    }

    private Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object processed = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            if (processed == null) {
                throw new BeansException(String.format("post processor after handler returns null when process bean '%s' by %s", beanName, beanPostProcessor.getClass().getName()));
            }
            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
            if (bean != processed) {
                logger.debug("Bean '{}' was replaced by post processor after handler {}.", beanName, beanPostProcessor.getClass().getName());
                bean = processed;
            }
        }
        return bean;
    }

    /**
     * 添加BeanPostProcessor
     */
    public abstract void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 获取所有BeanPostProcessor
     */
    public abstract List<BeanPostProcessor> getBeanPostProcessors();

    /**
     * 实例化所有非延迟加载的bean
     */
    public abstract void preInstantiateSingletons();
}
