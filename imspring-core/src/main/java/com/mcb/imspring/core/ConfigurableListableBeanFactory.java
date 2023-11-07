package com.mcb.imspring.core;

public interface ConfigurableListableBeanFactory extends BeanFactory{
    String[] getBeanNamesForType(Class<?> type);

}
