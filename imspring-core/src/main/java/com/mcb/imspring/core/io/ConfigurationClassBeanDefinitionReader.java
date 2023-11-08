package com.mcb.imspring.core.io;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationClassBeanDefinitionReader extends AbstractBeanDefinitionReader{

    public ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void loadBeanDefinitions(Set<Class<?>> configClasses) {
        for (Class<?> configClass : configClasses) {
            loadBeanDefinitionsForConfigurationClass(configClass);
        }
    }

    /**
     * 把配置类中带@Bean注解的方法注册为BeanDefinition
     */
    private void loadBeanDefinitionsForConfigurationClass(Class<?> configClass) {
        Set<Method> beanMethods = retrieveBeanMethodMetadata(configClass);
        if (beanMethods.isEmpty()) {
            return;
        }
        for (Method beanMethod : beanMethods) {
            Class<?> returnType = beanMethod.getReturnType();
            if (returnType.equals(Void.TYPE)) {
                throw new BeansException(String.format("@Bean method return type can not be void %s", beanMethod.getName()));
            }
            String beanName = BeanUtils.getBeanName(beanMethod);
            BeanDefinition def = createBeanDefinition(returnType, beanName);
            this.registry.registerBeanDefinition(beanName, def);
        }
    }

    /**
     * 检测配置类中带@Bean注解的方法
     */
    private Set<Method> retrieveBeanMethodMetadata(Class<?> sourceClass) {
        Set<Method> beanMethods = new HashSet<>();
        for(Method method : sourceClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                beanMethods.add(method);
            }
        }
        return beanMethods;
    }


}
