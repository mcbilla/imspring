package com.mcb.imspring.core.env;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment = PropertyResolver + Profile，Profile用于环境切换，PropertyResolver用于解析配置文件
 * 目前支持PropertyResolver
 */
public class StandardEnvironment implements Environment {

    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";

    private Map<String, Object> properties = new HashMap<>();

    private final PropertyResolver propertyResolver;

    public StandardEnvironment() {
        this.propertyResolver = new DefaultPropertyResolver(properties);
        prepareEnvironment();
    }

    /**
     * 初始化环境参数
     */
    private void prepareEnvironment() {
        this.properties.putAll(getSystemEnvironment());

        this.properties.putAll(getSystemProperties());
    }

    @Override
    public String[] getActiveProfiles() {
        return new String[0];
    }

    @Override
    public Map<String, Object> getSystemEnvironment() {
        return (Map) System.getenv();
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        return (Map) System.getProperties();
    }

    @Override
    public boolean containsProperty(String key) {
        return this.propertyResolver.containsProperty(key);
    }

    @Override
    public String getProperty(String key) {
        return this.propertyResolver.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return this.propertyResolver.getProperty(key, targetType);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return this.propertyResolver.getProperty(key, targetType, defaultValue);
    }

    @Override
    public String getRequiredProperty(String key) {
        return this.propertyResolver.getRequiredProperty(key);
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) {
        return this.propertyResolver.getRequiredProperty(key, targetType);
    }

    @Override
    public String resolvePlaceholders(String text) {
        return this.propertyResolver.resolvePlaceholders(text);
    }
}
