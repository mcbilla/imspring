package com.mcb.imspring.aop.test.aop;

import com.mcb.imspring.aop.test.service.IMyService;
import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.annotation.ComponentScan;

@ComponentScan("com.mcb.imspring.aop")
public class AopTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopTest.class)) {
            IMyService bean1 = context.getBean("myService");
            bean1.test();
        } catch (Exception e) {
            throw e;
        }
    }
}
