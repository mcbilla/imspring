package com.mcb.imspring.tx;

import com.mcb.imspring.core.common.MethodClassKey;
import com.mcb.imspring.core.utils.ReflectionUtils;
import com.mcb.imspring.tx.annotation.Transactional;
import com.mcb.imspring.tx.transaction.td.DefaultTransactionAttribute;
import com.mcb.imspring.tx.transaction.td.RollbackRuleAttribute;
import com.mcb.imspring.tx.transaction.td.TransactionAttribute;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事务属性源头，在容器初始化阶段缓存所有@Transaction修饰的方法对应的事务属性TransactionAttributeSource
 * 当某个@Transaction方法被调用的时候，从这里获取事务属性
 */
public class AnnotationTransactionAttributeSource implements TransactionAttributeSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final TransactionAttribute NULL_TRANSACTION_ATTRIBUTE = new DefaultTransactionAttribute() {
        @Override
        public String toString() {
            return "null";
        }
    };

    // key是MethodClassKey，用于唯一确认某个类中的某个方法，value是事务属性
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
                String methodIdentification = ReflectionUtils.getQualifiedMethodName(method, targetClass);
                DefaultTransactionAttribute dta = (DefaultTransactionAttribute) txAttr;
                dta.setDescriptor(methodIdentification);
                logger.debug("Adding transactional method '" + methodIdentification + "' with attribute: " + txAttr);
                this.attributeCache.put(cacheKey, txAttr);
            }
            return txAttr;
        }

    }

    protected Object getCacheKey(Method method, Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    /**
     * 计算某个类中某个方法的事务属性
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
