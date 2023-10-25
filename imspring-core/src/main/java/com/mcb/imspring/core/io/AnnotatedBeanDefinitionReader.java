package com.mcb.imspring.core.io;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
        // 加载有@Bean注解的方法
        loadBeanDefinitionsFromBean(configClass);

        // 如果标有ComponentScan注解，还需要扫描包路径下的所有类，加载有Component注解的类
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            loadBeanDefinitionsFromScan(configClass);
        }
    }

    /**
     * 扫描有@Bean注解的方法，方法名作为beanName
     */
    private void loadBeanDefinitionsFromBean(Class<?> configClass) {
        Method[] methods = configClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Bean.class)) {
                Class<?> returnType = method.getReturnType();
                if (returnType.equals(Void.TYPE)) {
                    throw new BeansException(String.format("return type could not be void %s", method));
                }
                String beanName = method.getName();
                BeanDefinition def = this.createBeanDefinition(returnType, beanName);
                this.registry.put(beanName, def);
            }
        }
    }

    /**
     * 扫描带有@Component注解的类，包括@Controller、@Service、@Repository
     */
    private void loadBeanDefinitionsFromScan(Class<?> configClass) throws IOException, URISyntaxException {
        ComponentScan anno = BeanUtils.findAnnotation(configClass, ComponentScan.class);
        String scanPackage = anno == null || anno.value().length() == 0 ? configClass.getPackage().getName() : anno.value();
        // 全路径类名集合
        Set<String> classNameSet = this.scan(scanPackage);
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
                String beanName = BeanUtils.getBeanName(clazz);
                BeanDefinition def = this.createBeanDefinition(clazz, beanName);
                this.registry.put(beanName, def);
            }
        }
    }

    /**
     * 从指定的路径加载该路径下所有类的全路径类名
     */
    private Set<String> scan(String scanPackage) throws IOException, URISyntaxException {
        logger.debug("component scan in packages: [{}]", scanPackage);
        List<String> classNameList = new ResourceLoader(scanPackage).scan();
        Set<String> classNameSet = new HashSet<>(classNameList);
        if (logger.isDebugEnabled()) {
            classNameSet.forEach((className) -> {
                logger.debug("found class by component scan: [{}]", className);
            });
        }
        return classNameSet;
    }

    private BeanDefinition createBeanDefinition(Class<?> clazz, String beanName) {
        if (this.registry.containsKey(beanName)) {
            throw new BeansException("Duplicate bean name: " + beanName);
        }
        logger.debug("create BeanDefinition beanName: [{}]，class: [{}]", beanName, clazz.getName());
        Constructor constructor = BeanUtils.getBeanConstructor(clazz);
        return new BeanDefinition(beanName, clazz, constructor);
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }
}
