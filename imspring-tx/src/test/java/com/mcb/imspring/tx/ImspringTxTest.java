package com.mcb.imspring.tx;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.annotation.ComponentScan;

@ComponentScan("com.mcb.imspring.tx")
public class ImspringTxTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringTxTest.class)) {
            TransactionInterceptor bean = context.getBean("transactionInterceptor");
        } catch (Exception e) {
            throw e;
        }
    }

}
