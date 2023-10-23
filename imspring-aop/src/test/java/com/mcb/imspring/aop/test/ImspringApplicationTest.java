package com.mcb.imspring.aop.test;

import com.mcb.imspring.aop.test.service.IMyService;
import com.mcb.imspring.aop.test.service.MySingleService;
import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.annotation.ComponentScan;

@ComponentScan("com.mcb.imspring.aop")
public class ImspringApplicationTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            IMyService bean1 = context.getBean("myService");
            bean1.test();

            MySingleService bean2 = context.getBean("mySingleService");
            bean2.test();
        } catch (Exception e) {
            throw e;
        }
    }
}
