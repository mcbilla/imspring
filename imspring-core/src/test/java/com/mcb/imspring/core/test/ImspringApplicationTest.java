package com.mcb.imspring.core.test;

import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.core.test.bean.BeanPostProcessorBean;

@ComponentScan("com.mcb.imspring.core.test.bean")
public class ImspringApplicationTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringApplicationTest.class)) {
            BeanPostProcessorBean bean = context.getBean("beanPostProcessorBean", BeanPostProcessorBean.class);
            System.out.println(bean);
        } catch (Exception e) {
            throw e;
        }
    }
}
