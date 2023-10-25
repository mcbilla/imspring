package com.mcb.imspring.core.test.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.core.test.bean.ComponentBean;
import com.mcb.imspring.core.test.bean.ServiceBean;

@Configuration
public class BeanConfig {
    @Bean
    public ServiceBean myServiceBean() {
        return new ServiceBean("11", 22);
    }

    @Bean
    public ComponentBean componentBean() {
        return new ComponentBean();
    }
}
