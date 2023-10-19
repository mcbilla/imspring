package com.mcb.imspring.core.test.context;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.ImspringApplicationTest;
import com.mcb.imspring.core.test.bean.BeanPostProcessorBean;
import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void testMyApplicationContext() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            BeanPostProcessorBean bean = context.getBean("beanPostProcessorBean", BeanPostProcessorBean.class);
            System.out.println(bean);
        } catch (Exception e) {
            throw e;
        }
    }
}
