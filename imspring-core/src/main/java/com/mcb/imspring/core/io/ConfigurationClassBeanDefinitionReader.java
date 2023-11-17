package com.mcb.imspring.core.io;

import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.support.ConfigurationClass;
import com.mcb.imspring.core.utils.BeanUtils;

import java.lang.reflect.Method;
import java.util.Set;

public class ConfigurationClassBeanDefinitionReader extends AbstractBeanDefinitionReader{

    public ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void loadBeanDefinitions(Set<ConfigurationClass> configClasses) {
        for (ConfigurationClass configClass : configClasses) {
            if (configClass.isImported()) {
                registerBeanDefinitionForImportedConfigurationClass(configClass);
            }
            for (Method beanMethod : configClass.getBeanMethods())
                loadBeanDefinitionsForBeanMethod(configClass, beanMethod);
        }
    }

    /**
     * 把 @Import 引入的类注册为 BeanDefinition
     */
    private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
        // TODO
    }

    /**
     * 把配置类中带 @Bean 注解的方法注册为 ConfigurationClassBeanDefinition
     */
    private void loadBeanDefinitionsForBeanMethod(ConfigurationClass configClass, Method beanMethod) {
        Class<?> returnType = beanMethod.getReturnType();
        if (returnType.equals(Void.TYPE)) {
            throw new BeansException(String.format("@Bean method return type can not be void %s", beanMethod.getName()));
        }
        String beanName = BeanUtils.getBeanName(beanMethod);
        BeanDefinition def = createBeanDefinition(beanName);
        def.setFactoryBeanName(configClass.getBeanName());
        def.setFactoryMethodName(beanMethod.getName());
        def.setTargetType(returnType);
        this.registry.registerBeanDefinition(beanName, def);
    }
}
