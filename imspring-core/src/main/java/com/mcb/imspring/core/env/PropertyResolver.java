package com.mcb.imspring.core.env;

import com.sun.istack.internal.Nullable;

/**
 * 用于加载配置文件的属性值
 */
public interface PropertyResolver {
    boolean containsProperty(String key);

    @Nullable
    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    @Nullable
    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    String getRequiredProperty(String key);

    <T> T getRequiredProperty(String key, Class<T> targetType);

    String resolvePlaceholders(String text);
}
