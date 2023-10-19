package com.mcb.imspring.core.io;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotatedBeanDefinitionReader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, BeanDefinition> registry = new ConcurrentHashMap<>();

    public void loadBeanDefinitions(Class<?> configClass) throws IOException, URISyntaxException {
        Set<String> classNameSet = scanForClassNames(configClass);
        if (classNameSet.isEmpty()) {
            return;
        }
        for (String className : classNameSet) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeansException(e);
            }
            if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface()) {
                continue;
            }
            int mod = clazz.getModifiers();
            if (Modifier.isAbstract(mod) || Modifier.isPrivate(mod)) {
                continue;
            }
            Component component = BeanUtils.findAnnotation(clazz, Component.class);
            if (component != null) {
                // 创建BeanDefinition
                String beanName = BeanUtils.getBeanName(clazz);
                if (this.registry.containsKey(beanName)) {
                    throw new BeansException("Duplicate bean name: " + beanName);
                }
                logger.debug("found component beanName: [{}]，className: [{}]", beanName, className);
                Constructor constructor = BeanUtils.getBeanConstructor(clazz);
                BeanDefinition def = new BeanDefinition(beanName, clazz, constructor);
                this.registry.put(def.getName(), def);
            }
        }
    }

    private Set<String> scanForClassNames(Class<?> configClass) throws IOException, URISyntaxException {
        // 获取扫描包路径
        ComponentScan scan = BeanUtils.findAnnotation(configClass, ComponentScan.class);
        String scanPackage = scan == null || scan.value().length() == 0 ? configClass.getPackage().getName() : scan.value();
        logger.debug("component scan in packages: [{}]", scanPackage);

        // 扫描包路径下的所有类
        List<String> classNameList = new ResourceLoader(scanPackage).scan();
        Set<String> classNameSet = new HashSet<>(classNameList);
        if (logger.isDebugEnabled()) {
            classNameSet.forEach((className) -> {
                logger.debug("found class by component scan: [{}]", className);
            });
        }
        return classNameSet;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }
}
