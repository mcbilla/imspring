package com.mcb.imspring.core;

import java.lang.annotation.Annotation;

public interface ListableBeanFactory extends BeanFactory{
    String[] getBeanNamesForType(Class<?> type);

}
