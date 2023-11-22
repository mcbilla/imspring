package com.mcb.imspring.core.env;

import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.BeanUtils;
import com.mcb.imspring.core.utils.YamlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 解析配置文件，目前只支持application.yml、application.properties
 * 支持解析@("${key:default}")，支持占位符和默认值
 */
public class DefaultPropertyResolver implements PropertyResolver {
    private static final String CONFIG_APP_YAML = "application.yml";

    private static final String CONFIG_APP_PROP = "application.properties";

    public static final String PLACEHOLDER_PREFIX = "${";

    public static final String PLACEHOLDER_SUFFIX = "}";

    public static final String VALUE_SEPARATOR = ":";

    private Map<String, Object> properties = new HashMap<>();

    public DefaultPropertyResolver() {
    }

    public DefaultPropertyResolver(Map<String, Object> properties) {
        this.properties = properties;
        prepareProperties();
    }

    private void prepareProperties() {
        // yml配置文件
        this.properties.putAll(getYamlConfig());

        // properties配置文件
        this.properties.putAll(getPropertiesConfig());
    }

    public Map<String, Object> getYamlConfig() {
        try {
            URL url = BeanUtils.getContextClassLoader().getResource(CONFIG_APP_YAML);
            if (url != null) {
                return YamlUtils.loadByPath(url.getPath(), true);
            }
        } catch (FileNotFoundException e) {
            throw new BeansException(e);
        }
        return null;
    }

    public Map<String, Object> getPropertiesConfig() {
        try {
            InputStream in = BeanUtils.getContextClassLoader().getResourceAsStream(CONFIG_APP_PROP);
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                return (Map) props;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public String getProperty(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key).toString();
        }
        return null;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return getProperty(key, targetType, null);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return (T) value;
        }
        return defaultValue;
    }

    @Override
    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) {
        T value = getProperty(key, targetType);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    @Override
    public String resolvePlaceholders(String text) {
        // 判断是否${...}格式
        if (!text.startsWith(PLACEHOLDER_PREFIX) || !text.endsWith(PLACEHOLDER_SUFFIX)) {
            return text;
        }
        int n = text.indexOf(VALUE_SEPARATOR);
        String key;
        String defaultValue = null;
        if (n == -1) {
            // no default value: ${key}
            key = text.substring(2, text.length() - 1);
        } else {
            // has default value: ${key:default}
            key = text.substring(2, n);
            defaultValue = text.substring(n + 1, text.length() - 1);
        }
        String property = getProperty(key);
        return property != null ? property : defaultValue;
    }
}
