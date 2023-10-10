package com.mcb.imspring.core.test.context;

import com.mcb.imspring.core.context.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.ImspringApplicationTest;
import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void testMyApplicationContext() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            Object bean = context.getBean("123");
        } catch (Exception e) {
            throw e;
        }
    }
}
