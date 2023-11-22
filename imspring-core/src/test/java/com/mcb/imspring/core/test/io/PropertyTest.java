package com.mcb.imspring.core.test.io;

import com.mcb.imspring.core.env.DefaultPropertyResolver;
import com.mcb.imspring.core.utils.YamlUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;

public class PropertyTest {

    @Test
    public void testYaml() throws FileNotFoundException {
//        ClassLoader classLoader = getClass().getClassLoader();
//        URL url = classLoader.getResource("application.yml");
//        System.out.println(url);

//        Map map = YamlUtils.loadByPath(url.getPath());
//        System.out.println(map);

//        Properties properties = YamlUtils.loadByPath(url.getPath(), Properties.class);
//        properties.getProperty("mcb.test")
//        System.out.println(properties);

//        Map<String, Object> map = YamlUtils.loadByPath(url.getPath(), true);
//        System.out.println(map);

        DefaultPropertyResolver resolver = new DefaultPropertyResolver();
        System.out.println(resolver.getYamlConfig());
    }

    @Test
    public void testProperties() {
        DefaultPropertyResolver resolver = new DefaultPropertyResolver();
        System.out.println(resolver.getPropertiesConfig());
    }
}
