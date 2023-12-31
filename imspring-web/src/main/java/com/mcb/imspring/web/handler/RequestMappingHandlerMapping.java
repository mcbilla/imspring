package com.mcb.imspring.web.handler;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.ApplicationContext;
import com.mcb.imspring.core.common.Ordered;
import com.mcb.imspring.core.context.ApplicationContextAware;
import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.core.utils.CollectionUtils;
import com.mcb.imspring.web.utils.WebUtils;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * RequestMappingHandlerMapping 主要负责在自身初始化阶段搜寻出当前容器内所有可用 Controller 实现，建立 url 和 HandlerMethod 的映射关系，并保存所有的 HandlerInterceptor
 * 当请求进来的时候，根据 url 匹配一个 HandlerMethod，加上所有的 HandlerInterceptor 封装成一个 HandlerExecutionChain 返回
 */
public class RequestMappingHandlerMapping implements HandlerMapping, InitializingBean, ApplicationContextAware, Ordered {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AnnotationConfigApplicationContext applicationContext;

    /**
     * url和handlerMethod的对应关系
     */
    private Map<String, Object> handlerMap = new LinkedHashMap<>();

    /**
     * 所有拦截器
     */
    private List<HandlerInterceptor> interceptors = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = (AnnotationConfigApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        initHandlers();
    }

    private void initHandlers() {
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            detectHandlers(beanName);
        }
    }

    private void detectHandlers(String beanName) {
        Object bean = applicationContext.getBean(beanName);
        if (bean == null) {
            return;
        }
        // 注册HandlerMethod
        if (WebUtils.isHandler(bean.getClass())) {
            Map<Method, Object> methodMap = selectMethods(bean.getClass(),
                    (method -> createHandlerMethod(beanName, method, bean)));
            methodMap.forEach((method, mapping) -> {
                registerHandlerMethod(beanName, method, mapping);
            });
        }
        // 注册HandlerInterceptor
        if (WebUtils.isHandlerInterceptor(bean.getClass())) {
            registerHandlerInterceptor(beanName, bean);
        }
    }

    private Map<Method, Object> selectMethods(Class<?> targetType, final Function<Method, Object> function) {
        final Map<Method, Object> methodMap = new LinkedHashMap<>();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (WebUtils.isRequestMapping(method)) {
                Object result = function.apply(method);
                methodMap.put(method, result);
            }
        }
        return methodMap;
    }

    private HandlerMethod createHandlerMethod(String beanName, Method method, Object handler) {
        return new HandlerMethod(beanName, method, handler);
    }

    private void registerHandlerMethod(String beanName, Method method, Object mapping) {
        String pattern = ((HandlerMethod) mapping).getPattern().pattern();
        logger.debug("register handler method url: [{}]，beanName: [{}], method: [{}]", pattern, beanName, method.getName());
        this.handlerMap.put(pattern, mapping);
    }

    private void registerHandlerInterceptor(String beanName, Object bean) {
        logger.debug("register handler interceptor beanName: [{}]", beanName);
        this.interceptors.add((HandlerInterceptor) bean);
    }

    @Override
    @Nullable
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        Object handler = getHandlerInternal(request);
        if (handler == null) {
            return null;
        }
        HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);
        return executionChain;
    }

    private Object getHandlerInternal(HttpServletRequest request) {
        return this.getMappingByPath(request.getRequestURI());
    }

    private HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain chain = new HandlerExecutionChain(handler);
        if (!CollectionUtils.isEmpty(this.interceptors)) {
            for (HandlerInterceptor interceptor : this.interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        return chain;
    }

    public Object getMappingByPath(String urlPath) {
        return this.handlerMap.get(urlPath);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}