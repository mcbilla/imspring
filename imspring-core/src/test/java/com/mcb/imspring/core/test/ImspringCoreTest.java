package com.mcb.imspring.core.test;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.bean.ComponentBean;
import com.mcb.imspring.core.test.bean.ServiceA;
import com.mcb.imspring.core.test.bean.ServiceB;
import com.mcb.imspring.core.test.config.ComponentScanConfig;
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
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ComponentScanConfig.class)) {
            ServiceA serviceA = context.getBean("serviceA", ServiceA.class);
            serviceA.test();

            ServiceB serviceB = context.getBean("serviceB", ServiceB.class);
            serviceB.test();
        } catch (Exception e) {
            throw e;
        }
    }
}
