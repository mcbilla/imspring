package com.mcb.imspring.core.context;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.exception.BeanCreationException;
import com.mcb.imspring.core.exception.BeanNotOfRequiredTypeException;
import com.mcb.imspring.core.exception.NoSuchBeanDefinitionException;
import com.mcb.imspring.core.exception.NoUniqueBeanDefinitionException;
import com.mcb.imspring.core.io.ResourceResolver;
import com.mcb.imspring.core.utils.BeanUtils;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext{
    /**
     * 传说中的ioc容器
     */
    protected final Map<String, BeanDefinition> ioc;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        // 1、扫描所有类
        final Set<String> beanClassNames = scanForClassNames(configClass);

        // 2、初始化扫描到的类，并且将它们放入到IOC容器之中
        this.ioc = createBeanDefinitions(beanClassNames);

        // 3、实例化IOC容器中的bean
        createBeanInstance();

        // 4、完成依赖注入
        beanAutowired();

        logger.debug("ApplicationContext init finish [{}]", ioc);
    }

    private Set<String> scanForClassNames(Class<?> configClass) {
        // 获取扫描包路径
        ComponentScan scan = BeanUtils.findAnnotation(configClass, ComponentScan.class);
        String scanPackage = scan == null || scan.value().length() == 0 ? configClass.getPackage().getName()  : scan.value();
        logger.debug("component scan in packages: [{}]", scanPackage);

        // 扫描包路径下的所有类
        ResourceResolver rr = new ResourceResolver(scanPackage);
        List<String> classNameList = rr.scan(resource -> {
            String name = resource.getName();
            if (!name.endsWith(".class")) {
                return null;
            }
            return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
        });
        Set<String> classNameSet = new HashSet<>(classNameList);
        if (logger.isDebugEnabled()) {
            classNameSet.forEach((className) -> {
                logger.debug("found class by component scan: [{}]", className);
            });
        }
        return classNameSet;
    }

    private Map<String, BeanDefinition> createBeanDefinitions(Set<String> classNameSet) {
        if (classNameSet.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, BeanDefinition> defMap = new HashMap<>();
        for (String className : classNameSet) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(e);
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
                if (defMap.containsKey(beanName)) {
                    throw new BeanCreationException("Duplicate bean name: " + beanName);
                }
                logger.debug("found component beanName: [{}]，className: [{}]", beanName, className);
                Constructor constructor = BeanUtils.getBeanConstructor(clazz);
                BeanDefinition def = new BeanDefinition(beanName, clazz, constructor);
                defMap.put(def.getName(), def);
            }
        }
        return defMap;
    }

    private void createBeanInstance() {
        if (this.ioc == null || this.ioc.isEmpty()) {
            return;
        }
        this.ioc.values().forEach(def -> {
            // 创建instance，这里暂时使用构造器创建
            try {
                Constructor cons = def.getConstructor();
                final Parameter[] parameters = cons.getParameters();
                Object[] args = new Object[parameters.length];
                def.setInstance(cons.newInstance(args));
            } catch (Exception e) {
                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
            }
        });
    }

    private void beanAutowired() {
        if (this.ioc == null || this.ioc.isEmpty()) {
            return;
        }
        // 通过字段和set方法注入依赖
        this.ioc.values().forEach(def -> {
            Object instance = def.getInstance();
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = null;
                if (autowired.value() != null && autowired.value().length() > 0) {
                    beanName = autowired.value();
                } else {
                    beanName = BeanUtils.getBeanName(field.getType().getSimpleName());
                }
                if (ioc.containsKey(beanName)) {
                    try {
                        field.setAccessible(true);
                        field.set(instance, ioc.get(beanName).getInstance());
                    } catch (IllegalAccessException e) {
                        throw new BeanCreationException(String.format("Exception when autowired '%s': %s", def.getName(), field.getName()), e);
                    }
                }
            }
        });
    }

    /**
     * 检测是否存在指定Name的Bean
     */
    @Override
    public boolean containsBean(String name) {
        return this.ioc.containsKey(name);
    }

    /**
     * 通过Name查找Bean，不存在时抛出NoSuchBeanDefinitionException
     */
    @Override
    public <T> T getBean(String name) {
        BeanDefinition def = this.ioc.get(name);
        if (def == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s'.", name));
        }
        return (T) def.getInstance();
    }

    /**
     * 通过Name和Type查找Bean，不存在抛出NoSuchBeanDefinitionException，存在但与Type不匹配抛出BeanNotOfRequiredTypeException
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        T t = findBean(name, requiredType);
        if (t == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s' and type '%s'.", name, requiredType));
        }
        return t;
    }

    /**
     * 通过Type查找Beans
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(requiredType);
        if (def == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with type '%s'.", requiredType));
        }
        return (T) def.getInstance();
    }

    /**
     * 通过Type查找Bean，不存在抛出NoSuchBeanDefinitionException，存在多个但缺少唯一@Primary标注抛出NoUniqueBeanDefinitionException
     */
    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        List<BeanDefinition> defs = findBeanDefinitions(requiredType);
        if (defs.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(defs.size());
        for (BeanDefinition def : defs) {
            list.add((T) def.getInstance());
        }
        return list;
    }

    /**
     * findXxx与getXxx类似，但不存在时返回null
     */
    @Nullable
    protected <T> T findBean(String name, Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(name, requiredType);
        if (def == null) {
            return null;
        }
        return (T) def.getInstance();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T findBean(Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(requiredType);
        if (def == null) {
            return null;
        }
        return (T) def.getInstance();
    }

    /**
     * 根据Type查找若干个BeanDefinition，返回0个或多个。
     */
    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return this.ioc.values().stream()
                .filter(def -> type.isAssignableFrom(def.getBeanClass()))
                .sorted().collect(Collectors.toList());
    }

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> defs = findBeanDefinitions(type);
        if (defs.isEmpty()) {
            throw new NoSuchBeanDefinitionException(String.format("no such bean with type '%s' found", type.getName()));
        }
        if (defs.size() > 1) {
            throw new NoUniqueBeanDefinitionException(String.format("Multiple bean with type '%s' found", type.getName()));
        }
        return defs.get(0);
    }

    /**
     * 根据Name查找BeanDefinition，如果Name不存在，返回null
     */
    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return this.ioc.get(name);
    }

    /**
     * 根据Name和Type查找BeanDefinition，如果Name不存在，返回null，如果Name存在，但Type不匹配，抛出异常。
     */
    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        BeanDefinition def = findBeanDefinition(name);
        if (def == null) {
            return null;
        }
        if (!requiredType.isAssignableFrom(def.getBeanClass())) {
            throw new BeanNotOfRequiredTypeException(String.format("Autowire required type '%s' but bean '%s' has actual type '%s'.", requiredType.getName(),
                    name, def.getBeanClass().getName()));
        }
        return def;
    }

    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition def) {
        return null;
    }

    @Override
    public void close() {
        System.out.println("自动关闭");
    }
}
