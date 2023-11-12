package com.mcb.imspring.core.common;

import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class Constants {
    private final String className;

    /** Map from String field name to object value. */
    private final Map<String, Object> fieldCache = new HashMap<>();

    public Constants(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        this.className = clazz.getName();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (ReflectionUtils.isPublicStaticFinal(field)) {
                String name = field.getName();
                try {
                    Object value = field.get(null);
                    this.fieldCache.put(name, value);
                }
                catch (IllegalAccessException ex) {
                    // just leave this field and continue
                }
            }
        }
    }

    public Set<Object> getValues(String namePrefix) {
        String prefixToUse = (namePrefix != null ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : "");
        Set<Object> values = new HashSet<>();
        this.fieldCache.forEach((code, value) -> {
            if (code.startsWith(prefixToUse)) {
                values.add(value);
            }
        });
        return values;
    }

    /**
     * Return the name of the analyzed class.
     */
    public final String getClassName() {
        return this.className;
    }

    /**
     * Return the number of constants exposed.
     */
    public final int getSize() {
        return this.fieldCache.size();
    }

    /**
     * Exposes the field cache to subclasses:
     * a Map from String field name to object value.
     */
    protected final Map<String, Object> getFieldCache() {
        return this.fieldCache;
    }
}
