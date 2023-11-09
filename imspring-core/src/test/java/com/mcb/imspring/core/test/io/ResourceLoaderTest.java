package com.mcb.imspring.core.test.io;

import com.mcb.imspring.core.io.DefaultResourceLoader;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ResourceLoaderTest {

    @Test
    public void testSystem() {
        System.out.println(System.getProperty("os.name").toLowerCase());
    }

    @Test
    public void testScan() throws IOException, URISyntaxException {
        List<String> classNames = new DefaultResourceLoader().scan("com.mcb.imspring");
        System.out.println(classNames);
    }
}
