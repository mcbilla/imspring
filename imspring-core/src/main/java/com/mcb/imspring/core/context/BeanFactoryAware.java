package com.mcb.imspring.core.context;

import com.mcb.imspring.core.BeanFactory;

public interface BeanFactoryAware {
    void setBeanFactory(BeanFactory beanFactory);
}
