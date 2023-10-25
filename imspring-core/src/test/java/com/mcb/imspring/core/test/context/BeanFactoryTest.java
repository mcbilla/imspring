package com.mcb.imspring.core.test.context;

import com.mcb.imspring.core.BeanFactory;
import com.mcb.imspring.core.DefaultListableBeanFactory;
import com.mcb.imspring.core.test.bean.ServiceBean;
import com.mcb.imspring.core.test.config.BeanConfig;
import org.junit.Test;

public class BeanFactoryTest {
    @Test
    public void test() {
        BeanFactory beanFactory = new DefaultListableBeanFactory(BeanConfig.class);
        ServiceBean bean = beanFactory.getBean("myServiceBean");
        bean.test();
    }
}
