package com.mcb.imspring.core.env;

import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Environment = PropertyResolver + Profile，Profile用于环境切换，PropertyResolver用于解析配置文件
 * 目前支持PropertyResolver
 */
public class StandardEnvironment implements Environment {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";

    private final Set<String> activeProfiles = new LinkedHashSet<>();

    private final PropertyResolver propertyResolver;

    public StandardEnvironment() {
        this(new DefaultPropertyResolver());
    }

    public StandardEnvironment(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void setActiveProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        logger.debug("Activating profiles " + Arrays.toString(profiles));
        synchronized (this.activeProfiles) {
            this.activeProfiles.clear();
            for (String profile : profiles) {
                validateProfile(profile);
                this.activeProfiles.add(profile);
            }
        }
    }

    @Override
    public void addActiveProfile(String profile) {
        logger.debug("Activating profile '" + profile + "'");
        validateProfile(profile);
        synchronized (this.activeProfiles) {
            this.activeProfiles.add(profile);
        }
    }

    @Override
    public String[] getActiveProfiles() {
        synchronized (this.activeProfiles) {
            if (this.activeProfiles.isEmpty()) {
                String profiles = doGetActiveProfilesProperty();
                if (StringUtils.hasText(profiles)) {
                    setActiveProfiles(StringUtils.commaDelimitedListToStringArray(
                            StringUtils.trimAllWhitespace(profiles)));
                }
            }
        }
        return StringUtils.toStringArray(this.activeProfiles);
    }

    protected String doGetActiveProfilesProperty() {
        return getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    protected void validateProfile(String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
        }
        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
        }
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
