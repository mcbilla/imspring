package com.mcb.imspring.core.support;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ConfigurationClass {
    private String beanName;

    private Class<?> beanClass;

    private final Set<Method> beanMethods = new LinkedHashSet<>();

    private final Set<ConfigurationClass> importedBy = new LinkedHashSet<>(1);

    public ConfigurationClass(String beanName, Class<?> beanClass) {
        this.beanName = beanName;
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void addBeanMethod(Method method) {
        this.beanMethods.add(method);
    }

    public Set<Method> getBeanMethods() {
        return this.beanMethods;
    }

    public boolean isImported() {
        return !this.importedBy.isEmpty();
    }

    public void mergeImportedBy(ConfigurationClass otherConfigClass) {
        this.importedBy.addAll(otherConfigClass.importedBy);
    }

    public Set<ConfigurationClass> getImportedBy() {
        return this.importedBy;
    }
}
