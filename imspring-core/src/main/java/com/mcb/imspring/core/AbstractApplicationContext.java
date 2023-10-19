package com.mcb.imspring.core;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanPostProcessor;
import com.mcb.imspring.core.exception.BeansException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public abstract class AbstractApplicationContext implements ApplicationContext, AutoCloseable{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BeanFactory beanFactory;

    public AbstractApplicationContext(Class<?> configClass) {
        this.beanFactory = new DefaultListableBeanFactory(configClass);
        refresh();
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        // 1、扫描所有类
//        final Set<String> beanClassNames = scanForClassNames(configClass);
//
//        // 2、初始化扫描到的类，并且将它们放入到IOC容器之中，此时还没有实例化
//        createBeanDefinitions(beanClassNames);
//
//        // 3、实例化IOC容器中的bean
//        createBeanInstance();
//
//        // 4、BeanPostProcessor前置处理
//        applyBeanPostProcessorsBeforeInstantiation();
//
//        // TODO 5、InitializingBean处理
//
//        // TODO 6、init-method
//
//        // 7、依赖注入
//        autowireBean();
//
//        // 8、BeanPostProcessor后置处理
//        applyBeanPostProcessorsAfterInitialization();
//
//        logger.debug("BeanFactory init finish [{}]", ioc);
    }

    protected void createBeanInstance() {
//        if (this.ioc == null || this.ioc.isEmpty()) {
//            return;
//        }
//        this.ioc.values().forEach(def -> {
//            // 创建instance，这里暂时使用构造器创建
//            try {
//                Constructor cons = def.getConstructor();
//                final Parameter[] parameters = cons.getParameters();
//                Object[] args = new Object[parameters.length];
//                Object bean = cons.newInstance(args);
//                def.setInstance(bean);
//
//                if (bean instanceof BeanFactoryAware) {
//                    ((BeanFactoryAware) bean).setBeanFactory(this);
//                }
//
//                if (bean instanceof BeanPostProcessor) {
//                    beanPostProcessors.add((BeanPostProcessor) bean);
//                }
//            } catch (Exception e) {
//                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
//            }
//        });
    }

    protected void applyBeanPostProcessorsBeforeInstantiation() {
//        this.ioc.values().stream()
//                .filter(this::isBeanPostProcessorDefinition)
//                .sorted()
//                .forEach(def -> {
//                    BeanPostProcessor processor = (BeanPostProcessor) def.getInstance();
//                    Object processed = processor.postProcessBeforeInitialization(def.getInstance(), def.getName());
//                    if (processed == null) {
//                        throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", def.getName(), processor));
//                    }
//                    // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
//                    if (def.getInstance() != processed) {
//                        logger.debug("Bean '{}' was replaced by post processor before handler {}.", def.getName(), processor.getClass().getName());
//                        def.setInstance(processed);
//                    }
//                });
    }

    protected void autowireBean() {
//        if (this.ioc == null || this.ioc.isEmpty()) {
//            return;
//        }
//        // 通过字段和set方法注入依赖
//        this.ioc.values().forEach(def -> {
//            Object instance = def.getInstance();
//            Field[] fields = instance.getClass().getDeclaredFields();
//            for (Field field : fields) {
//                if (!field.isAnnotationPresent(Autowired.class)) {
//                    continue;
//                }
//                Autowired autowired = field.getAnnotation(Autowired.class);
//                String beanName = null;
//                if (autowired.value() != null && autowired.value().length() > 0) {
//                    beanName = autowired.value();
//                } else {
//                    beanName = BeanUtils.getBeanName(field.getType().getSimpleName());
//                }
//                if (ioc.containsKey(beanName)) {
//                    try {
//                        field.setAccessible(true);
//                        field.set(instance, ioc.get(beanName).getInstance());
//                    } catch (IllegalAccessException e) {
//                        throw new BeanCreationException(String.format("Exception when autowired '%s': %s", def.getName(), field.getName()), e);
//                    }
//                }
//            }
//        });
    }

    protected void applyBeanPostProcessorsAfterInitialization() {
//        this.ioc.values().stream()
//                .filter(this::isBeanPostProcessorDefinition)
//                .sorted()
//                .forEach(def -> {
//                    BeanPostProcessor processor = (BeanPostProcessor) def.getInstance();
//                    Object processed = processor.postProcessAfterInitialization(def.getInstance(), def.getName());
//                    if (processed == null) {
//                        throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", def.getName(), processor));
//                    }
//                    // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用
//                    if (def.getInstance() != processed) {
//                        logger.debug("Bean '{}' was replaced by post processor after handler {}.", def.getName(), processor.getClass().getName());
//                        def.setInstance(processed);
//                    }
//                });
    }

    private boolean isBeanPostProcessorDefinition(BeanDefinition definition) {
        return BeanPostProcessor.class.isAssignableFrom(definition.getBeanClass());
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public <T> T getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        return getBeans(requiredType);
    }

    @Override
    public BeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    public void close() {
        System.out.println("自动关闭");
    }
}
