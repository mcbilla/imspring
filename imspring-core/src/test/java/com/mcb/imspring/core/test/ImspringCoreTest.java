package com.mcb.imspring.core.test;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.bean.ComponentBean;
import org.junit.Test;

public class ImspringCoreTest {
    @Test
    public void test() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.mcb.imspring.core")) {
            ComponentBean bean = context.getBean("componentBean", ComponentBean.class);
            bean.test();
        } catch (Exception e) {
            throw e;
        }
    }
}
