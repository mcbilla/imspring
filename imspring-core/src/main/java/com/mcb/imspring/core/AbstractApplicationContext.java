package com.mcb.imspring.core;

import com.mcb.imspring.core.context.ApplicationContextAwareProcessor;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.exception.BeansException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ApplicationContext, BeanDefinitionRegistry, AutoCloseable{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long startupDate;

    /** Flag that indicates whether this context is currently active. */
    private final AtomicBoolean active = new AtomicBoolean();

    /** Flag that indicates whether this context has been closed already. */
    private final AtomicBoolean closed = new AtomicBoolean();

    private final Class<?> configClass;

    private DefaultListableBeanFactory beanFactory;

    public AbstractApplicationContext(Class<?> configClass) {
        this.configClass = configClass;
    }

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

        // 4、实例化所有非延迟加载的单例
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

    /**
     * 初始化 BeanFactory，加载并解析配置，这时候 BeanFactory 还没有实例化
     */
    private DefaultListableBeanFactory obtainFreshBeanFactory() {
        return new DefaultListableBeanFactory(configClass);
    }

    /**
     * 对 BeanFactory 进行功能增强，如设置BeanFactory的类加载器，添加几个 BeanPostProcessor，手动注册几个特殊的 bean
     */
    private void prepareBeanFactory(DefaultListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }

    /**
     * 实例化所有非延迟加载的单例
     */
    private void finishBeanFactoryInitialization(DefaultListableBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
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
    public void close() {
        this.closed.set(true);
        this.active.set(false);
        logger.debug("ApplicationContext quit, total run [{}] ms", (System.currentTimeMillis() - startupDate));
    }
}
