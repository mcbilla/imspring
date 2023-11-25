package com.mcb.imspring.tx.sync;

import com.mcb.imspring.core.common.NamedThreadLocal;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.tx.jdbc.ConnectionHolder;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 事务同步管理器，使用ThreadLocal存储当前线程的数据库连接、事务同步器等
 */
public abstract class TransactionSynchronizationManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionSynchronizationManager.class);

    private static final ThreadLocal<Map<Object, Object>> resources =
            new NamedThreadLocal<>("Transactional resources");

    public static boolean hasResource(Object key) {
        return (getResource(key) != null);
    }

    @Nullable
    public static Object getResource(Object key) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static void bindResource(Object key, Object value) throws IllegalStateException {
        Assert.notNull(value, "Value must not be null");
        Map<Object, Object> map = resources.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }
        Object oldValue = map.put(key, value);
        logger.debug("Bind transaction resource key: [{}], value: [{}]", key, value instanceof ConnectionHolder ? ((ConnectionHolder)value).getConnection() : value);
        if (oldValue != null) {
            throw new IllegalStateException(
                    "Already value [" + oldValue + "] for key [" + key + "] bound to thread");
        }
    }

    public static Object unbindResource(Object key) throws IllegalStateException {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        Object value = map.remove(key);
        // Remove entire ThreadLocal if empty...
        if (map.isEmpty()) {
            resources.remove();
        }
        logger.debug("Unbind transaction resource key: [{}], value: [{}]", key, value instanceof ConnectionHolder ? ((ConnectionHolder)value).getConnection() : value);
        if (value == null) {
            throw new IllegalStateException("No value for key [" + key + "] bound to thread");
        }
        return value;
    }
}
