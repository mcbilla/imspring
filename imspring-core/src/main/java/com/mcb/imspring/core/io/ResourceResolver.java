package com.mcb.imspring.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;

/**
 * 资源解析器
 */
public class ResourceResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String basePackage;

    private List<String> classNameList;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    public List<String> scan() {
        try {
            scan0(basePackage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return classNameList;
    }

    private void scan0(String basePackage) throws IOException, URISyntaxException {
        logger.debug("scan path: {}", basePackage);
//        URL url = null;
//        if (isWindows()) {
//            url = this.getContextClassLoader().getResource("\\" + basePackage.replaceAll("\\.", "\\\\"));
//        } else {
//            url = this.getContextClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
//        }
//        File classDir = new File(url.getFile());
//        for (File file : classDir.listFiles()) {
//            if (file.isDirectory()) {
//                scan0(basePackage + "." + file.getName());
//            } else {
//                if (!file.getName().endsWith(".class")) {
//                    continue;
//                }
//                String clazzName = (basePackage + "." + file.getName().replace(".class", ""));
//                classNameList.add(clazzName);
//            }
//        }

        Enumeration<URL> en = this.getContextClassLoader().getResources(basePackage.replaceAll("\\.", "/"));

        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            URI uri = url.toURI();
            String uriStr = removeTrailingSlash(uriToString(uri));
            String uriBaseStr = uriStr.substring(0, uriStr.length() - basePackage.length());
            if (uriBaseStr.startsWith("file:")) {
                uriBaseStr = uriBaseStr.substring(5);
            }
            File classDir = new File(uriBaseStr);
            if (classDir.isDirectory()) {
                System.out.println("这是文件夹" + uriBaseStr);
            } else {
                System.out.println("这不是文件夹" + uriBaseStr);
            }
        }
    }


    /**
     * ClassLoader首先从Thread.getContextClassLoader()获取，如果获取不到，再从当前Class获取，
     * 因为Web应用的ClassLoader不是JVM提供的基于Classpath的ClassLoader，而是Servlet容器提供的ClassLoader，
     * 它不在默认的Classpath搜索，而是在/WEB-INF/classes目录和/WEB-INF/lib的所有jar包搜索，
     * 从Thread.getContextClassLoader()可以获取到Servlet容器专属的ClassLoader
     * @return
     */
    private ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }

    String uriToString(URI uri) throws UnsupportedEncodingException {
        return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8.toString());
    }

    String removeLeadingSlash(String s) {
        if (s.startsWith("/") || s.startsWith("\\")) {
            s = s.substring(1);
        }
        return s;
    }

    String removeTrailingSlash(String s) {
        if (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
