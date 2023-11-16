package com.mcb.imspring.tx.transaction.td;

import com.mcb.imspring.core.common.MethodClassKey;
import com.mcb.imspring.core.utils.ReflectionUtils;
import com.mcb.imspring.tx.annotation.Transactional;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于解析@Transaction注解，获取注解的事务属性
 */
public class AnnotationTransactionAttributeSource implements TransactionAttributeSource{
    private static final TransactionAttribute NULL_TRANSACTION_ATTRIBUTE = new DefaultTransactionAttribute() {
        @Override
        public String toString() {
            return "null";
        }
    };

    private final Map<Object, TransactionAttribute> attributeCache = new ConcurrentHashMap<>(1024);

    public AnnotationTransactionAttributeSource() {
    }

    @Override
    public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Object cacheKey = getCacheKey(method, targetClass);
        TransactionAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            if (cached == NULL_TRANSACTION_ATTRIBUTE) {
                return null;
            }
            else {
                return cached;
            }
        } else {
            TransactionAttribute txAttr = computeTransactionAttribute(method, targetClass);
            if (txAttr == null) {
                this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
            } else {

            }
            return txAttr;
        }

    }

    protected Object getCacheKey(Method method, Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    /**
     * 定义某个类中某个方法的事务属性
     * @return
     */
    private TransactionAttribute computeTransactionAttribute(Method method, Class<?> targetClass) {
        // 非 public 方法不能开启事务
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // 查找方法的事务属性，主要是查找方法是否有 @Transaction 注解
        TransactionAttribute txAttr = findTransactionAttribute(method);
        if (txAttr != null) {
            return txAttr;
        }

        return null;
    }

    /**
     * 查找方法是否有 @Transactional，有的话封装成 TransactionAttribute 进行返回
     */
    private TransactionAttribute findTransactionAttribute(Method method) {
        Transactional anno = ReflectionUtils.findAnnotation(method, Transactional.class);
        if (anno == null) {
            return null;
        }
        return parseTransactionAnnotation(anno);
    }

    /**
     * 把 @Transaction 封装成 TransactionAttribute
     */
    protected TransactionAttribute parseTransactionAnnotation(Transactional anno) {
        DefaultTransactionAttribute ta = new DefaultTransactionAttribute();
        ta.setPropagationBehavior(anno.propagation().value());
        ta.setIsolationLevel(anno.isolation().value());
        List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
        for (Class<?> rbRule : anno.rollbackFor()) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }
        ta.setRollbackRules(rollbackRules);
        return ta;
    }

}
