package com.mcb.imspring.aop.test;

import com.mcb.imspring.aop.test.service.MyService;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.AnnotationConfigApplicationContext;

@ComponentScan("com.mcb.imspring.aop")
public class ImspringApplicationTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            MyService bean = context.getBean("myService", MyService.class);
            bean.test();
            System.out.println(bean);
        } catch (Exception e) {
            throw e;
        }
    }
}
