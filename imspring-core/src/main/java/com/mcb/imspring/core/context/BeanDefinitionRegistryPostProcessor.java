package com.mcb.imspring.core.context;

/**
 * 假如我们需要增加一个 BeanDefinition，并且对容器中的所有 BeanDefinition 进行增强处理，这种情况增加动作必须要在增强动作前面发生
 * 这时候增加 BeanDefinition 动作的类就可以实现 BeanDefinitionRegistryPostProcessor 接口
 * BeanDefinitionRegistryPostProcessor 可以保证在普通 BeanFactoryPostProcessor 之前执行
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry);
}
