package com.mcb.imspring.aop.test;

import com.mcb.imspring.aop.test.service.IMyService;
import com.mcb.imspring.aop.test.service.MyServiceImpl;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.AnnotationConfigApplicationContext;

@ComponentScan("com.mcb.imspring.aop")
public class ImspringApplicationTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            IMyService bean = context.getBean("myService", MyServiceImpl.class);
            bean.test();
        } catch (Exception e) {
            throw e;
        }
    }
}
