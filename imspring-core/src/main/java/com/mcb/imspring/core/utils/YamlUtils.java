package com.mcb.imspring.core.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlUtils {

    /**
     * 从指定路径加载加载YAML
     */
    public static Map<String, Object> loadByPath(String path) throws FileNotFoundException {
        return loadByPath(path, false);
    }

    /**
     * 从指定路径加载加载YAML，并判断是否进行扁平化处理
     */
    public static Map<String, Object> loadByPath(String path, boolean isPlain) throws FileNotFoundException {
        Map<String, Object> fullMap = loadByPath(path, LinkedHashMap.class);
        if (!isPlain) {
            return fullMap;
        }
        Map<String, Object> plainMap = new LinkedHashMap<>();
        convertToPlain(fullMap, "", plainMap);
        return plainMap;
    }

    /**
     * 从指定路径加载加载YAML，返回指定类型
     */
    public static <T> T loadByPath(String path, Class<T> type) throws FileNotFoundException {
        return load(new FileInputStream(path), type);
    }

    public static <T> T load(InputStream in, Class<T> type) throws FileNotFoundException {
        return load(new InputStreamReader(in), type);
    }

    /**
     * 加载YAML
     * @param reader    {@link Reader}
     * @param type      加载的Bean类型，即转换为的bean
     * @return
     * @param <T>       Bean类型，默认map
     */
    public static <T> T load(Reader reader, Class<T> type) {
        Assert.notNull(reader, "Reader must be not null !");
        if (null == type) {
            //noinspection unchecked
            type = (Class<T>) Object.class;
        }
        final Yaml yaml = new Yaml();
        try {
            return yaml.loadAs(reader, type);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {

            }
        }
    }

    private static void convertToPlain(Map<String, Object> source, String prefix, Map<String, Object> plain) {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (value instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) value;
                convertToPlain(subMap, prefix + key + ".", plain);
            } else if (value instanceof List) {
                plain.put(prefix + key, value);
            } else {
                plain.put(prefix + key, value.toString());
            }
        }
    }
}
