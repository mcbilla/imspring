package com.mcb.imspring.tx.test;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.tx.test.entity.User;
import com.mcb.imspring.tx.test.service.TxService;
import org.junit.Test;

@ComponentScan("com.mcb.imspring.tx")
public class ImspringTxTest {

    @Test
    public void test() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringTxTest.class)) {
            TxService bean = context.getBean("txService");
//            User query = bean.query();
//            System.out.println("查询结果=" + query);

//            int update = bean.update();
//            System.out.println("更新结果=" + update);

            int tx = bean.tx();
            System.out.println("事务结果=" + tx);
        } catch (Exception e) {
            throw e;
        }
    }

}
