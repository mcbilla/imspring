package com.mcb.imspring.web;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.context.ApplicationContextAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.web.handler.HandlerExecutionChain;
import com.mcb.imspring.web.handler.HandlerMapping;
import com.mcb.imspring.web.handler.HandlerMethod;
import com.mcb.imspring.web.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Component
public class RequestMappingHandlerMapping<T> implements HandlerMapping, InitializingBean, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AnnotationConfigApplicationContext applicationContext;

    /**
     * url和method的对应关系
     */
    private Map<String, T> pathLookup = new HashMap<>();

    /**
     * beanName和method的对应关系
     */
    private Map<String, List<T>> nameLookup = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = (AnnotationConfigApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        initHandlerMethods();
    }

    private void initHandlerMethods() {
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            detectHandlerMethods(beanName);
        }
    }

    private void detectHandlerMethods(String beanName) {
        Object bean = applicationContext.getBeanDefinition(beanName).getBean();
        if (WebUtils.isHandler(bean.getClass())) {
            Map<Method, T> methodMap = selectMethods(bean.getClass(),
                    (method -> (T) createHandlerMethod(beanName, method, bean)));
            methodMap.forEach((method, mapping) -> {
                registerHandlerMethod(beanName, method, mapping);
            });
        }
    }

    private Map<Method, T> selectMethods(Class<?> targetType, final Function<Method, T> function) {
        final Map<Method, T> methodMap = new LinkedHashMap<>();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (WebUtils.isRequestMapping(method)) {
                T result = function.apply(method);
                methodMap.put(method, result);
            }
        }
        return methodMap;
    }

    private HandlerMethod createHandlerMethod(String beanName, Method method, Object handler) {
        return new HandlerMethod(beanName, method, handler);
    }

    private void registerHandlerMethod(String beanName, Method method, T mapping) {
        String pattern = ((HandlerMethod) mapping).getPattern().pattern();
        logger.debug("register url: {}，beanName: {}, method: {}", pattern, beanName, method.getName());
        this.pathLookup.put(pattern, mapping);
        this.nameLookup.compute(beanName, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(mapping);
            return v;
        });
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        return null;
    }

    public T getMappingByPath(String urlPath) {
        return this.pathLookup.get(urlPath);
    }

    public List<T> getMappingsByBeanName(String beanName) {
        return this.nameLookup.get(beanName);
    }
}