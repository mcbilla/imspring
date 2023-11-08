package com.mcb.imspring.core.io;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.support.ConfigurationClassPostProcessor;
import com.mcb.imspring.core.support.AutowiredAnnotationBeanPostProcessor;

/**
 * 这里会把一些 PostProcessor 放入 BeanDefinition，包括
 * 1、调用 {@link ConfigurationClassPostProcessor} 处理 @Configuration、@Bean
 * 2、调用 {@link AutowiredAnnotationBeanPostProcessor} 处理带 @Autowired
 */
public class AnnotatedBeanDefinitionReader extends AbstractBeanDefinitionReader{
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.mcb.imspring.core.support.ConfigurationClassPostProcessor";

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.mcb.imspring.core.support.AutowiredAnnotationBeanPostProcessor";

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        this.registerAnnotationConfigProcessors(this.registry);
    }

    private void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        // 注册ConfigurationClassPostProcessor
        BeanDefinition def = createBeanDefinition(ConfigurationClassPostProcessor.class, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
        registry.registerBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME, def);

        // TODO 注册AutowiredAnnotationBeanPostProcessor
    }
}
