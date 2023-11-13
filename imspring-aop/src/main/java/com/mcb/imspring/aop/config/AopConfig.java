package com.mcb.imspring.aop.config;

import com.mcb.imspring.aop.AnnotationAwareAspectJAutoProxyCreator;
import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;

@Configuration
public class AopConfig {
    @Bean
    public AnnotationAwareAspectJAutoProxyCreator aspectJAwareAdvisorAutoProxyCreator() {
        return new AnnotationAwareAspectJAutoProxyCreator();
    }
}
