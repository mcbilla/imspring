package com.mcb.imspring.web.test.handler;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.web.test.config.ComponentScanConfig;
import org.junit.Test;

public class RequestMappingTest {
    @Test
    public void test() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ComponentScanConfig.class)) {
        } catch (Exception e) {
            throw e;
        }
    }
}
