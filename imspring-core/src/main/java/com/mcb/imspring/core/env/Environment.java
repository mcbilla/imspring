package com.mcb.imspring.core.env;

import java.util.Map;

public interface Environment extends PropertyResolver{
    String[] getActiveProfiles();

    Map<String, Object> getSystemEnvironment();

    Map<String, Object> getSystemProperties();
}
