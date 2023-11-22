package com.mcb.imspring.core.resource;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.context.BeanDefinition;
import com.mcb.imspring.core.context.BeanDefinitionRegistry;
import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.BeanUtils;
import com.mcb.imspring.core.utils.CollectionUtils;
import com.mcb.imspring.core.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

public class ClassPathBeanDefinitionScanner extends AbstractBeanDefinitionReader{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultResourceLoader loader = new DefaultResourceLoader();

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void scan(String... basePackages) {
        logger.debug("component scan in packages: [{}]", basePackages);
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            // 从指定的路径加载该路径下所有类的全路径类名并过滤
            Map<String, Class<?>> candidateClasses = findCandidateClasses(basePackage);

            // 查找待注册的BeanDefinition
            Set<BeanDefinition> candidates = findCandidateComponents(candidateClasses);
            beanDefinitions.addAll(candidates);
        }
        beanDefinitions.forEach(def -> {
            logger.debug("register BeanDefinition: [{}]", def.getName());
            this.registry.registerBeanDefinition(def.getName(), def);
        });
    }

    public Map<String, Class<?>> findCandidateClasses(String basePackage) {
        Assert.notNull(basePackage, "basePackage can not be null");
        Map<String, Class<?>> candidateClasses = new HashMap<>();
        try {
            List<String> classNameList = loader.scan(basePackage);
            classNameList.forEach(className -> {
                Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new BeansException(e);
                }
                if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface()) {
                    return;
                }
                int mod = clazz.getModifiers();
                if (Modifier.isAbstract(mod) || Modifier.isPrivate(mod)) {
                    return;
                }
                logger.debug("found class by component scan: [{}]", className);
                candidateClasses.put(className, clazz);
            });
        } catch (Exception e) {
            throw new BeansException(e);
        }
        return candidateClasses;
    }

    private Set<BeanDefinition> findCandidateComponents(Map<String, Class<?>> candidateClasses) {
        Set<BeanDefinition> defs = new HashSet<>();
        if (CollectionUtils.isEmpty(candidateClasses)) {
            return defs;
        }
        for (Map.Entry<String, Class<?>> entry : candidateClasses.entrySet()) {
            Class<?> clazz = entry.getValue();
            // 扫描带有@Component注解的类，包括@Controller、@Service、@Repository
            Component component = ReflectionUtils.findAnnotation(clazz, Component.class);
            if (component != null) {
                String beanName = BeanUtils.getBeanName(clazz);
                BeanDefinition def = createBeanDefinition(beanName, clazz);
                defs.add(def);
            }
        }
        return defs;
    }
}
