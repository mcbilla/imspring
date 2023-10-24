package com.mcb.imspring.core.test.context;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.DefaultListableBeanFactory;
import com.mcb.imspring.core.test.ImspringApplicationTest;
import com.mcb.imspring.core.test.bean.ServiceBean;
import org.junit.Test;

public class BeanFactoryTest {
    @Test
    public void test() {
        BeanFactory beanFactory = new DefaultListableBeanFactory(ImspringApplicationTest.class);
        ServiceBean bean = beanFactory.getBean("myServiceBean");
        bean.test();
    }
}
