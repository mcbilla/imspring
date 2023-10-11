package com.mcb.imspring.core.test.context;

import com.mcb.imspring.core.context.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.ImspringApplicationTest;
import com.mcb.imspring.core.test.bean.ComponentBean;
import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void testMyApplicationContext() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            ComponentBean bean = context.getBean("componentBean", ComponentBean.class);
            System.out.println(bean);
        } catch (Exception e) {
            throw e;
        }
    }
}
