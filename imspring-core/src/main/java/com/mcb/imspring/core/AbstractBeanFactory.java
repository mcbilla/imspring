package com.mcb.imspring.core;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Value;
import com.mcb.imspring.core.context.*;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.*;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring IOC容器的的抽象类，实现ConfigurableBeanFactory的接口
 *
 *
 * Spring Bean 的生命周期
 * 1、实例化 Instantiation
 * 2、属性赋值 Populate
 * 3、初始化 Initialization
 * 4、销毁 Destruction
 */
public abstract class AbstractBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, SingletonBeanRegistry {
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
        try {
            BeanDefinition def = getBeanDefinition(name, requiredType);
            Assert.notNull(def, String.format("No such BeanDefinition defined with name: %s, type: %s", name, requiredType));
            return doGetBean(name, requiredType, null);
        } catch (Exception e) {
            throw new BeansException(String.format("get bean fail, name: %s, type: %s", name, requiredType), e);
        }
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) throws BeansException {
        List<BeanDefinition> defs = getBeanDefinitions(requiredType);
        List<T> list = new ArrayList<>(defs.size());
        for (BeanDefinition def : defs) {
            T bean = getBean(def.getName());
            if (bean != null) {
                list.add(bean);
            }
        }
        return list;
    }

    @Override
    public Class<?> getType(String name) {
        Object beanInstance = getSingleton(name, false);
        if (beanInstance != null) {
            return beanInstance.getClass();
        }
        BeanDefinition bd = getBeanDefinition(name);
        if (bd != null) {
            return bd.getTargetType();
        }
        return null;
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        List<BeanDefinition> beanDefinitions = this.getBeanDefinitions(type);
        if (!CollectionUtils.isEmpty(beanDefinitions)) {
            List<String> beanNames = beanDefinitions.stream().map(BeanDefinition::getName).collect(Collectors.toList());
            return beanNames.toArray(new String[0]);
        }
        return null;
    }

    public <T> T doGetBean(String beanName, @Nullable Class<T> requiredType, @Nullable Object[] args) throws InvocationTargetException, IllegalAccessException {
        BeanDefinition def = getBeanDefinition(beanName, requiredType);
        if (beanName == null) {
            beanName = def.getName();
        }

        Object beanInstance = getSingleton(beanName);
        if (beanInstance != null) {
            // 如果找到实例，不管是否正在初始化，都直接返回
            if (isSingletonCurrentlyInCreation(beanName)) {
                logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
                        "' that is not fully initialized yet - a consequence of a circular reference");
            }
            return (T) beanInstance;
        } else {
            // 如果没找到实例，就走创建流程
            String finalBeanName = beanName;
            beanInstance = getSingleton(beanName, () -> {
                return createBean(finalBeanName, def);
            });
        }

        logger.debug("Bean finish instantiate: [{}]", beanName);

        return (T) beanInstance;
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "Bean name must not be null");
        Object singletonObject = this.getSingleton(beanName);
        if (singletonObject == null) {
            // 设置bean正在初始化的标记
            beforeSingletonCreation(beanName);

            // 真正实例化bean
            singletonObject = singletonFactory.getObject();

            // 清除bean正在初始化的标记
            afterSingletonCreation(beanName);

            // 清除二级和三级缓存，把bean添加到一级缓存
            addSingleton(beanName, singletonObject);
        }

        return singletonObject;
    }

    private Object createBean(String beanName, BeanDefinition def) {
        try {
            // 创建bean
            Object beanInstance = createBeanInstance(beanName, def);

            // 这里是解决循环依赖的关键，
            // 第一次getSingleton的时候，执行到这里，earlySingletonExposure一定为true，会把bean的工厂方法添加到三级缓存，相当于提前暴露bean
            // 如果产生了循环依赖，第二次getSingleton的时候从三级缓存创建bean，放入二级缓存，如果没有产生循环依赖就跳过这一步
            // 最后addSingleton，清除一级缓存和二级缓存的信息，把bean放入一级缓存
            boolean earlySingletonExposure = isSingletonCurrentlyInCreation(beanName);
            if (earlySingletonExposure) {
                Object finalBeanInstance = beanInstance;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, def, finalBeanInstance));
            }

            Object exposedObject = beanInstance;

            // 属性填充
            populateBean(exposedObject, beanName);

            // 初始化bean
            exposedObject = initializeBean(exposedObject, beanName, def);

            // 这一步非常的巧妙，目的是为了保证本方法返回的bean和其他对象依赖的bean是同一个对象
            // 1、没有发生循环依赖，且没有AOP。那就没有经过二级缓存，earlySingletonReference一定为null。这里返回的exposedObject就是初始创建的bean，放到一级缓存，其他bean在后面初始化的时候也从一级缓存里面取出该bean使用。
            // 2、发生了循环依赖，没有AOP。因为没有发生AOP，exposedObject并没有经过代理，exposedObject == beanInstance恒成立；因为发生了循环依赖，earlySingletonReference不为null，等于从二级缓存拿到的bean。赋值exposedObject = earlySingletonReference，保证返回的bean和其他bean依赖的bean其实都是二级缓存的bean。
            // 3、发生了循环依赖，且发生了AOP。虽然exposedObject经过了代理，但是注意initializeBean返回的并不是代理的对象，而是AbstractAutoProxyCreator的earlyProxyReferences缓存的原始bean，所以exposedObject == beanInstance也是成立的！！！另外earlySingletonReference不为null，等于从二级缓存拿到的bean。这样返回的bean和其他bean依赖的bean其实都是二级缓存的bean，和第二点的区别是，这个二级缓存的bean是一个提前创建的代理对象。
            if (earlySingletonExposure) {
                Object earlySingletonReference = getSingleton(beanName, false);
                if (earlySingletonReference != null && exposedObject == beanInstance) {
                    exposedObject = earlySingletonReference;
                }
            }

            return exposedObject;
        } catch (Exception e) {
            throw new BeansException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
        }
    }

    /**
     * 如果发生了AOP，就返回代理对象，如果没有发生就返回原来的对象，这里相当于给AOP留了一个钩子
     */
    private Object getEarlyBeanReference(String beanName, BeanDefinition def, Object bean) {
        Object exposedObject = bean;
        List<BeanPostProcessor> beanPostProcessors = this.getBeanPostProcessors();
        if (!beanPostProcessors.isEmpty()) {
            for (BeanPostProcessor bp : beanPostProcessors) {
                if (bp instanceof InstantiationAwareBeanPostProcessor) {
                    exposedObject = ((InstantiationAwareBeanPostProcessor) bp).getEarlyBeanReference(exposedObject, beanName);
                }
            }
        }
        return exposedObject;
    }

    /**
     * 初始化bean实例，这里分两种情况
     * 1、如果是 @Bean 注入的 Bean，使用 factoryBean + factoryMethod 创建实例
     * 2、如果是 @Component 注入的 Bean，使用 beanClass 的默认构造器创建实例
     */
    private Object createBeanInstance(String beanName, BeanDefinition def) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (def.getBeanClass() != null) {
            // 使用 beanClass 的构造器创建
            Constructor<?> cons = BeanUtils.getBeanConstructor(def.getBeanClass());
            final Parameter[] parameters = cons.getParameters();
            Object[] args = new Object[parameters.length];
            return cons.newInstance(args);
        } else {
            // 使用指定 bean 和 method 创建
            String factoryBeanName = def.getFactoryBeanName();
            Object factoryBean = getBean(factoryBeanName);
            String factoryMethodName = def.getFactoryMethodName();
            Class<?>[] argumentTypes = def.getArgumentTypes();
            if (argumentTypes.length == 0) {
                Method factoryMethod = factoryBean.getClass().getDeclaredMethod(factoryMethodName);
                return factoryMethod.invoke(factoryBean);
            } else {
                Method factoryMethod = factoryBean.getClass().getDeclaredMethod(factoryMethodName, argumentTypes);
                Object[] args = new Object[argumentTypes.length];
                for (int i = 0; i < argumentTypes.length; i++) {
                    args[i] = this.getBean(argumentTypes[i]);
                }
                return factoryMethod.invoke(factoryBean, args);
            }
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
     *
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
            Method initMethod = ReflectionUtils.findMethod(def.getBeanClass(), def.getInitMethodName());
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
                logger.debug("Bean [{}] was replaced by post processor after handler [{}]", beanName, beanPostProcessor.getClass().getName());
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

    protected abstract Object getSingleton(String beanName, boolean allowEarlyReference);

    protected abstract boolean isSingletonCurrentlyInCreation(String beanName);

    protected abstract void beforeSingletonCreation(String beanName);

    protected abstract void afterSingletonCreation(String beanName);

    protected abstract void addSingleton(String beanName, Object singletonObject);

    protected abstract void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory);
}
