package com.mcb.imspring.core.io;

import com.mcb.imspring.core.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

/**
 * 资源解析器
 */
public class ResourceResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String basePackage;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    public <R> List<R> scan(Function<Resource, R> mapper) {
        try {
            List<R> collector = new ArrayList<>();
            scan0(basePackage, collector, mapper);
            return collector;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private <R> void scan0(String basePackage, List<R> collector, Function<Resource, R> mapper) throws IOException, URISyntaxException {
        logger.debug("scan path: {}", basePackage);
        Enumeration<URL> en = this.getContextClassLoader().getResources(basePackage.replaceAll("\\.", "/"));

        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            URI uri = url.toURI();
            String uriStr = ResourceUtils.removeTrailingSlash(ResourceUtils.uriToString(uri));
            if (uriStr.startsWith("file:")) {
                uriStr = uriStr.substring(5);
            }
            // 根路径，test/classes和test/classes
            String baseStr = ResourceUtils.removeLeadingSlash(uriStr.substring(0, uriStr.length() - basePackage.length()));

            Files.walk(Paths.get(uri)).filter(Files::isRegularFile).forEach(file -> {
                // 类文件绝对路径
                String path = file.toString();
                // 全路径类名
                String name = ResourceUtils.removeLeadingSlash(path.substring(baseStr.length()));
                Resource resource = new Resource(name, path);
                logger.debug("found resource=[{}]", resource);
                R r = mapper.apply(resource);
                if (r != null) {
                    collector.add(r);
                }
            });

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

}
