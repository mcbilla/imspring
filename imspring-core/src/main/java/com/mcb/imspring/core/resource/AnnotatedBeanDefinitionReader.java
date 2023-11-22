package com.mcb.imspring.core.resource;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.support.ConfigurationClassPostProcessor;
import com.mcb.imspring.core.support.AutowiredAnnotationBeanPostProcessor;
import com.mcb.imspring.core.utils.BeanUtils;


public class AnnotatedBeanDefinitionReader extends AbstractBeanDefinitionReader{
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.mcb.imspring.core.support.ConfigurationClassPostProcessor";

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.mcb.imspring.core.support.AutowiredAnnotationBeanPostProcessor";

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        this.registerAnnotationConfigProcessors(this.registry);
    }

    /**
     * 这里会把一些 BeanPostProcessor 和 BeanFactoryPostProcessor 提前放入 BeanDefinition，包括
     * 1、调用 {@link ConfigurationClassPostProcessor} 处理 @Configuration、@Bean
     * 2、调用 {@link AutowiredAnnotationBeanPostProcessor} 处理带 @Autowired
     */
    private void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        // 注册ConfigurationClassPostProcessor
        BeanDefinition def = createBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME, ConfigurationClassPostProcessor.class);
        registry.registerBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME, def);

        // TODO 注册AutowiredAnnotationBeanPostProcessor
    }

    /**
     * 指定类注册 BeanDefinition
     */
    public void register(Class<?>... componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }

    public void registerBean(Class<?> beanClass) {
        String beanName = BeanUtils.getBeanName(beanClass);
        BeanDefinition beanDef = createBeanDefinition(beanName, beanClass);
        this.registry.registerBeanDefinition(beanName, beanDef);
    }
}
