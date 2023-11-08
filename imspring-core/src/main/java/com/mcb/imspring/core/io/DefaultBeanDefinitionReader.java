package com.mcb.imspring.core.io;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认 BeanDefinition 注册器，读取主类上 @ComponentScan 注解
 * Spring 中是没有这个注册器的，Spring 的 @ComponentScan 注解是通过 ConfigurationClassPostProcessor 解析，这里做了简化处理
 */
public class DefaultBeanDefinitionReader extends AbstractBeanDefinitionReader{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DefaultBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void loadBeanDefinitions(Class<?> configClass) throws IOException, URISyntaxException {
        // 扫描ComponentScan注解包路径下的所有类，加载有Component注解的类
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            throw new BeansException(String.format("can not find component scan path {}", configClass.getName()));
        }
        loadBeanDefinitionsFromScan(configClass);
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
                BeanDefinition def = createBeanDefinition(clazz, beanName);
                this.registry.registerBeanDefinition(beanName, def);
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

}
