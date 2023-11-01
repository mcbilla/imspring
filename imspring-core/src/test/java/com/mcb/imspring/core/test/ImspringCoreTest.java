package com.mcb.imspring.core.test;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.DefaultListableBeanFactory;
import com.mcb.imspring.core.test.bean.ComponentBean;
import com.mcb.imspring.core.test.bean.ServiceBean;
import com.mcb.imspring.core.test.config.BeanConfig;
import com.mcb.imspring.core.test.config.ComponentScanConfig;
import org.junit.Test;

public class ImspringCoreTest {
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

    @Test
    public void test2() {
        BeanFactory beanFactory = new DefaultListableBeanFactory(BeanConfig.class);
        ServiceBean bean = beanFactory.getBean("myServiceBean");
        bean.test();
    }
}
