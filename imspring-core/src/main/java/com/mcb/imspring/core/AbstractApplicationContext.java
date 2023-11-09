package com.mcb.imspring.core;

import com.mcb.imspring.core.context.*;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.support.ApplicationContextAwareProcessor;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext, BeanDefinitionRegistry{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long startupDate;

    /** Flag that indicates whether this context is currently active. */
    private final AtomicBoolean active = new AtomicBoolean();

    /** Flag that indicates whether this context has been closed already. */
    private final AtomicBoolean closed = new AtomicBoolean();

    private DefaultListableBeanFactory beanFactory;

    public AbstractApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    /**
     * 这个队列默认是空的，用户可以向队列添加自定义 BeanFactoryPostProcessor
     */
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    /**
     * ApplicationContext的核心方法，实例化beanFactory的所有bean
     * @throws BeansException
     * @throws IllegalStateException
     */
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        // 1、初始化 refresh 的上下文环境
        prepareRefresh();

        // 2、初始化 BeanFactory，加载并解析配置
        this.beanFactory = this.obtainFreshBeanFactory();

        /*--至此，已经完成了简单容器的所有功能，下面开始对简单容器进行增强--*/

        // 3、对 BeanFactory 进行功能增强
        prepareBeanFactory(beanFactory);

        // 4、执行 BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors(beanFactory);

        // 5、注册 BeanPostProcessor
        registerBeanPostProcessors(beanFactory);

        // 5、实例化所有非延迟加载的单例
        finishBeanFactoryInitialization(beanFactory);
    }

    /**
     * 初始化 refresh 的上下文环境，就是记录下容器的启动时间、标记已启动状态、处理配置文件中的占位符
     */
    private void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);
        logger.debug("ApplicationContext startup");
    }

    protected DefaultListableBeanFactory obtainFreshBeanFactory() {
        return this.beanFactory;
    }

    /**
     * 对 BeanFactory 进行功能增强，如设置BeanFactory的类加载器，添加几个 BeanPostProcessor，手动注册几个特殊的 bean
     */
    private void prepareBeanFactory(DefaultListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }

    /**
     * 处理 BeanFactoryPostProcessor 接口
     * 默认只有 ConfigurationClassPostProcessor 实现了 eanFactoryPostProcessor 接口
     */
    private void invokeBeanFactoryPostProcessors(DefaultListableBeanFactory beanFactory) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class);
        if (postProcessorNames == null || postProcessorNames.length == 0) {
            return;
        }
        for (String ppName : postProcessorNames) {
            this.beanFactoryPostProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
        }
        if (!CollectionUtils.isEmpty(this.beanFactoryPostProcessors)) {
            // 分成两种Processor：BeanFactoryPostProcessor 和 BeanDefinitionRegistryPostProcessor
            // 先执行 BeanDefinitionRegistryPostProcessor 的 postProcessBeanDefinitionRegistry
            // 再执行 BeanFactoryPostProcessor + BeanDefinitionRegistryPostProcessor 的 postProcessBeanFactory
            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
            List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();

            for (BeanFactoryPostProcessor postProcessor : this.beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryProcessor =
                            (BeanDefinitionRegistryPostProcessor) postProcessor;
                    registryProcessors.add(registryProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }

            invokeBeanDefinitionRegistryPostProcessors(registryProcessors, beanFactory);

            invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
            invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        }
    }

    private void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, DefaultListableBeanFactory beanFactory) {
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanDefinitionRegistry(beanFactory);
        }
    }

    private void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, DefaultListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 把 BeanPostProcessor 类型的 BeanDefinition 注册到 registry
     */
    private void registerBeanPostProcessors(DefaultListableBeanFactory beanFactory) {
        List<BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions(BeanPostProcessor.class);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                beanFactory.addBeanPostProcessor(getBean(beanDefinition.getName()));
            }
        }
    }

    /**
     * 实例化所有非延迟加载的单例
     */
    private void finishBeanFactoryInitialization(DefaultListableBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
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
        return getBeanFactory().getBeans(requiredType);
    }

    @Override
    public Class<?> getType(String name) {
        return getBeanFactory().getType(name);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return ((DefaultListableBeanFactory)getBeanFactory()).getBeanNamesForType(type);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return ((DefaultListableBeanFactory)getBeanFactory()).containsBeanDefinition(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        return ((DefaultListableBeanFactory)getBeanFactory()).getBeanDefinition(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> type) {
        return ((DefaultListableBeanFactory)getBeanFactory()).getBeanDefinition(type);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name, Class<?> requiredType) {
        return ((DefaultListableBeanFactory)getBeanFactory()).getBeanDefinition(name, requiredType);
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions(Class<?> type) {
        return ((DefaultListableBeanFactory)getBeanFactory()).getBeanDefinitions(type);
    }

    @Override
    public BeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanFactory.getBeanDefinitionNames();
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws BeansException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    @Override
    public void close() {
        this.closed.set(true);
        this.active.set(false);
        logger.debug("ApplicationContext quit, total run [{}] ms", (System.currentTimeMillis() - startupDate));
    }
}
