package com.mcb.imspring.core.env;

import java.util.Map;

public interface Environment extends PropertyResolver{
    void setActiveProfiles(String... profiles);

    void addActiveProfile(String profile);

    String[] getActiveProfiles();

    Map<String, Object> getSystemEnvironment();

    Map<String, Object> getSystemProperties();
}
