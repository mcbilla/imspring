package com.mcb.imspring.core.test.context;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.bean.ComponentBean;
import com.mcb.imspring.core.test.config.BeanConfig;
import com.mcb.imspring.core.test.config.ComponentScanConfig;
import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void test() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class)) {
            ComponentBean bean = context.getBean("componentBean", ComponentBean.class);
            bean.test();
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void test1() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ComponentScanConfig.class)) {
            ComponentBean bean = context.getBean("componentBean", ComponentBean.class);
            bean.test();
        } catch (Exception e) {
            throw e;
        }
    }
}
