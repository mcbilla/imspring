package com.mcb.imspring.tx.test;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.tx.TransactionInterceptor;
import com.mcb.imspring.tx.test.service.TxService;

@ComponentScan("com.mcb.imspring.tx")
public class ImspringTxTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringTxTest.class)) {
            TxService bean = context.getBean("txService");
            bean.hello();
        } catch (Exception e) {
            throw e;
        }
    }

}
