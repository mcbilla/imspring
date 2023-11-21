package com.mcb.imspring.aop.test;

import com.mcb.imspring.aop.test.service.IMyService;
import com.mcb.imspring.aop.test.service.MySingleService;
import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.annotation.ComponentScan;

@ComponentScan("com.mcb.imspring.aop")
public class ImspringAopTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringAopTest.class)) {
            IMyService bean1 = context.getBean("myService");
            bean1.test();
//            MySingleService bean2 = context.getBean("mySingleService");
//            bean2.test();
//            String res = bean2.test("111");
//            System.out.println(res);
//            bean2.hello();
        } catch (Exception e) {
            throw e;
        }
    }
}
