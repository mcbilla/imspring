package com.mcb.imspring.web.test.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.ComponentScan;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.web.test.interceptor.LogInterceptor;

@ComponentScan("com.mcb.imspring")
@Configuration
public class WebConfig {

    @Bean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor();
    }
}
