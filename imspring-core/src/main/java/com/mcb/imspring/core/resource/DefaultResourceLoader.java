package com.mcb.imspring.core.resource;

import com.mcb.imspring.core.exception.BeansException;
import com.mcb.imspring.core.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DefaultResourceLoader implements ResourceLoader{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String baseStr;

    private ClassLoader classLoader;

    public DefaultResourceLoader() {
        if (this.classLoader == null) {
            this.classLoader = getClassLoader();
        }
    }

    public List<String> scan(String basePackage) throws IOException, URISyntaxException {
        logger.debug("scan path: {}", basePackage);
        List<String> collector = new ArrayList<>();

        Enumeration<URL> resources = classLoader.getResources(basePackage.replaceAll("\\.", "/"));
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            // url路径，例如/xxx/target/test-classes/com/mcb/imspring
            String urlPath = url.getPath();
            // 根路径，等于url路径 - basePackage，例如/xxx/target/test-classes
            this.baseStr = ResourceUtils.removeLeadingSlash(urlPath.substring(0, urlPath.length() - basePackage.length()));

            Files.walk(Paths.get(url.toURI())).filter(Files::isRegularFile).forEach(path -> {
                try {
                    Resource resource = getResource(path.toString());
                    String name = resource.getName();
                    if (name.endsWith(".class")) {
                        name =  name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
                        collector.add(name);
                    }
                } catch (MalformedURLException e) {
                    throw new BeansException(e);
                }
            });
        }
        return collector;
    }

    @Override
    public Resource getResource(String location) throws MalformedURLException {
        String name = ResourceUtils.removeLeadingSlash(location.substring(baseStr.length()));
        location = ResourceUtils.removeLeadingSlash(location);
        Resource resource = new FileResource(name, location);
        logger.debug("found resource=[{}]", resource);
        return resource;
    }

    @Override
    public ClassLoader getClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }
}
