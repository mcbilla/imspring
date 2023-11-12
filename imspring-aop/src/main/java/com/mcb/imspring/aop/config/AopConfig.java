package com.mcb.imspring.aop.config;

import com.mcb.imspring.aop.AspectJAwareAdvisorAutoProxyCreator;
import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;

@Configuration
public class AopConfig {
    @Bean
    public AspectJAwareAdvisorAutoProxyCreator aspectJAwareAdvisorAutoProxyCreator() {
        return new AspectJAwareAdvisorAutoProxyCreator();
    }
}
