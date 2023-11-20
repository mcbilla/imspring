package com.mcb.imspring.core.context;

import com.mcb.imspring.core.exception.BeansException;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}
