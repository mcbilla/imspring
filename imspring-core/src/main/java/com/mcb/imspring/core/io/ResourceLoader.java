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
import java.util.List;

public class ResourceLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String basePackage;

    private String baseStr;

    private ClassLoader classLoader;

    public ResourceLoader(String basePackage) {
        this.basePackage = basePackage;
        if (this.classLoader == null) {
            this.classLoader = getClassLoader();
        }
    }

    public List<String> scan() throws IOException, URISyntaxException {
        logger.debug("scan path: {}", basePackage);
        URL url = classLoader.getResource(basePackage.replaceAll("\\.", "/"));
        URI uri = url.toURI();
        String uriStr = ResourceUtils.removeTrailingSlash(ResourceUtils.uriToString(uri));
        if (uriStr.startsWith("file:")) {
            uriStr = uriStr.substring(5);
        }
        // 根路径，test/classes和test/classes
        this.baseStr = ResourceUtils.removeLeadingSlash(uriStr.substring(0, uriStr.length() - basePackage.length()));

        List<String> collector = new ArrayList<>();
        Files.walk(Paths.get(uri)).filter(Files::isRegularFile).forEach(path -> {
            Resource resource = getResource(path.toString());
            String name = resource.getName();
            if (name.endsWith(".class")) {
                name =  name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
                collector.add(name);
            }
        });
        return collector;
    }

    public Resource getResource(String location) {
        String name = ResourceUtils.removeLeadingSlash(location.substring(baseStr.length()));
        Resource resource = new Resource(name, location);
        logger.debug("found resource=[{}]", resource);
        return resource;
    }

    public ClassLoader getClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }
}
