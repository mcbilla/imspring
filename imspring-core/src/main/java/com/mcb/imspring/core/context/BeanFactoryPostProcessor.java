package com.mcb.imspring.core.context;

import com.mcb.imspring.core.ConfigurableListableBeanFactory;

/**
 * BeanFactoryPostProcessor 在 BeanDefinition 注册之后，Bean 实例化之前执行，用于对 Bean 的定义进行预处理。
 * 例如修改 Bean 的属性值、添加额外的配置信息等
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}
