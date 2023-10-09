package com.mcb.imspring.core.io;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * 资源解析器
 */
public class ResourceResolver {
    private String basePackage;

    private List<String> classNameList;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    public List<String> scan() {
        scan0(basePackage);
        return classNameList;
    }

    private void scan0(String basePackage) {
        URL url = this.getContextClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                scan0(basePackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String clazzName = (basePackage + "." + file.getName().replace(".class", ""));
                classNameList.add(clazzName);
            }
        }
    }

    ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }
}
