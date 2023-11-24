package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.core.utils.StringUtils;
import com.mcb.imspring.tx.exception.DataAccessException;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Bean 字段和数据库字段自动映射，支持字段名一样/驼峰式/下划线式对应
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {
    @Nullable
    private Class<T> mappedClass;

    private Map<String, Field> mappedFields;

    private Set<String> mappedProperties;

    public BeanPropertyRowMapper() {
    }

    public BeanPropertyRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    private void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<>();
        this.mappedProperties = new HashSet<>();

        for (Field f : mappedClass.getFields()) {
            String name = f.getName();
            String actualName = obtainActualFieldName(name);
            this.mappedFields.put(actualName, f);
            this.mappedProperties.add(actualName);
        }
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T bean;
        try {
            Constructor<T> constructor = this.mappedClass.getConstructor();
            bean = constructor.newInstance();
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            for (int i = 0; i < columns; i++) {
                String label = meta.getColumnLabel(i);
                String acutalName = obtainActualFieldName(label);
                Field field = this.mappedFields.get(acutalName);
                if (field != null) {
                    field.set(bean, rs.getObject(label));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Could not map result set to class %s", this.mappedClass.getName()), e);
        }
        return bean;
    }

    /**
     * 先获取全小写名称，再获取下划线名称（原来是驼峰式才会转下划线）
     * 如果两者不一致，说明原来是驼峰式，优先返回下划线名称，否则返回全小写名称
     */
    private String obtainActualFieldName(String name) {
        String lowerCaseName = lowerCaseName(name);
        String underscoreName = underscoreName(name);
        if (lowerCaseName.equals(underscoreName)) {
            return underscoreName;
        } else {
            return lowerCaseName;
        }
    }

    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    protected String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            }
            else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
