package com.mcb.imspring.core.context;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.exception.BeanCreationException;
import com.mcb.imspring.core.io.ResourceResolver;
import com.mcb.imspring.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Spring IOC容器的默认实现类，对应Spring的DefaultSingletonBeanRegistry
 */
public class DefaultBeanFactory extends AbstractBeanFactory{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // TODO 可以改成CopyOnWriteArrayList
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public DefaultBeanFactory(Class<?> configClass) {
        // 1、扫描所有类
        final Set<String> beanClassNames = scanForClassNames(configClass);

        // 2、初始化扫描到的类，并且将它们放入到IOC容器之中，此时还没有实例化
        this.ioc = createBeanDefinitions(beanClassNames);

        // 3、实例化IOC容器中的bean
        createBeanInstance();

        // 4、BeanPostProcessor前置处理
        applyBeanPostProcessorsBeforeInstantiation();

        // TODO 5、InitializingBean处理

        // TODO 6、init-method

        // 7、依赖注入
        autowireBean();

        // 8、BeanPostProcessor后置处理
        applyBeanPostProcessorsAfterInitialization();

        logger.debug("BeanFactory init finish [{}]", ioc);
    }

    protected Set<String> scanForClassNames(Class<?> configClass) {
        // 获取扫描包路径
        ComponentScan scan = BeanUtils.findAnnotation(configClass, ComponentScan.class);
        String scanPackage = scan == null || scan.value().length() == 0 ? configClass.getPackage().getName()  : scan.value();
        logger.debug("component scan in packages: [{}]", scanPackage);

        // 扫描包路径下的所有类
        ResourceResolver rr = new ResourceResolver(scanPackage);
        List<String> classNameList = rr.scan(resource -> {
            String name = resource.getName();
            if (!name.endsWith(".class")) {
                return null;
            }
            return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
        });
        Set<String> classNameSet = new HashSet<>(classNameList);
        if (logger.isDebugEnabled()) {
            classNameSet.forEach((className) -> {
                logger.debug("found class by component scan: [{}]", className);
            });
        }
        return classNameSet;
    }

    protected Map<String, BeanDefinition> createBeanDefinitions(Set<String> classNameSet) {
        if (classNameSet.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, BeanDefinition> defMap = new HashMap<>();
        for (String className : classNameSet) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(e);
            }
            if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface()) {
                continue;
            }
            int mod = clazz.getModifiers();
            if (Modifier.isAbstract(mod) || Modifier.isPrivate(mod)) {
                continue;
            }
            Component component = BeanUtils.findAnnotation(clazz, Component.class);
            if (component != null) {
                // 创建BeanDefinition
                String beanName = BeanUtils.getBeanName(clazz);
                if (defMap.containsKey(beanName)) {
                    throw new BeanCreationException("Duplicate bean name: " + beanName);
                }
                logger.debug("found component beanName: [{}]，className: [{}]", beanName, className);
                Constructor constructor = BeanUtils.getBeanConstructor(clazz);
                BeanDefinition def = new BeanDefinition(beanName, clazz, constructor);
                defMap.put(def.getName(), def);
            }
        }
        return defMap;
    }

    protected void createBeanInstance() {
        if (this.ioc == null || this.ioc.isEmpty()) {
            return;
        }
        this.ioc.values().forEach(def -> {
            // 创建instance，这里暂时使用构造器创建
            try {
                Constructor cons = def.getConstructor();
                final Parameter[] parameters = cons.getParameters();
                Object[] args = new Object[parameters.length];
                Object bean = cons.newInstance(args);
                def.setInstance(bean);

                if (bean instanceof BeanFactoryAware) {
                    ((BeanFactoryAware) bean).setBeanFactory(this);
                }

                if (bean instanceof BeanPostProcessor) {
                    beanPostProcessors.add((BeanPostProcessor) bean);
                }
            } catch (Exception e) {
                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
            }
        });
    }

    protected void applyBeanPostProcessorsBeforeInstantiation() {
        this.ioc.values().stream()
                .filter(this::isBeanPostProcessorDefinition)
                .sorted()
                .forEach(def -> {
                    BeanPostProcessor processor = (BeanPostProcessor) def.getInstance();
                    Object processed = processor.postProcessBeforeInitialization(def.getInstance(), def.getName());
                    if (processed == null) {
                        throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", def.getName(), processor));
                    }
                    // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
                    if (def.getInstance() != processed) {
                        logger.debug("Bean '{}' was replaced by post processor before handler {}.", def.getName(), processor.getClass().getName());
                        def.setInstance(processed);
                    }
                });
    }

    protected void autowireBean() {
        if (this.ioc == null || this.ioc.isEmpty()) {
            return;
        }
        // 通过字段和set方法注入依赖
        this.ioc.values().forEach(def -> {
            Object instance = def.getInstance();
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = null;
                if (autowired.value() != null && autowired.value().length() > 0) {
                    beanName = autowired.value();
                } else {
                    beanName = BeanUtils.getBeanName(field.getType().getSimpleName());
                }
                if (ioc.containsKey(beanName)) {
                    try {
                        field.setAccessible(true);
                        field.set(instance, ioc.get(beanName).getInstance());
                    } catch (IllegalAccessException e) {
                        throw new BeanCreationException(String.format("Exception when autowired '%s': %s", def.getName(), field.getName()), e);
                    }
                }
            }
        });
    }

    protected void applyBeanPostProcessorsAfterInitialization() {
        this.ioc.values().stream()
                .filter(this::isBeanPostProcessorDefinition)
                .sorted()
                .forEach(def -> {
                    BeanPostProcessor processor = (BeanPostProcessor) def.getInstance();
                    Object processed = processor.postProcessAfterInitialization(def.getInstance(), def.getName());
                    if (processed == null) {
                        throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", def.getName(), processor));
                    }
                    // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
                    if (def.getInstance() != processed) {
                        logger.debug("Bean '{}' was replaced by post processor after handler {}.", def.getName(), processor.getClass().getName());
                        def.setInstance(processed);
                    }
                });
    }

    private boolean isBeanPostProcessorDefinition(BeanDefinition definition) {
        return BeanPostProcessor.class.isAssignableFrom(definition.getBeanClass());
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }
}
