package com.mcb.imspring.core.test.io;

import com.mcb.imspring.core.io.ResourceResolver;
import org.junit.Test;

import java.util.List;

public class ResourceResolverTest {

    @Test
    public void testSystem() {
        System.out.println(System.getProperty("os.name").toLowerCase());
    }

    @Test
    public void testScan() {
        List<String> classNames = new ResourceResolver("com.mcb.imspring").scan(resource -> {
            String name = resource.getName();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        });
        System.out.println(classNames);
    }
}
