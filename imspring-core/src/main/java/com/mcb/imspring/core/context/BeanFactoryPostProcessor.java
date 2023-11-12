package com.mcb.imspring.core.context;

import com.mcb.imspring.core.ConfigurableListableBeanFactory;

/**
 * BeanFactoryPostProcessor 在 BeanDefinition 注册之后，Bean 实例化之前执行，扩展或是修改当前已经加载好的 BeanDefinition，用于对 Bean 的定义进行修改。
 * 例如增加或者修改 BeanDefinition，修改 BeanFactory 的其他配置信息
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}
