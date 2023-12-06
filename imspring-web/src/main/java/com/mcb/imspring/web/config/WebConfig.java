package com.mcb.imspring.web.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.web.handler.RequestMappingHandlerAdapter;
import com.mcb.imspring.web.handler.RequestMappingHandlerMapping;

@Configuration
public class WebConfig {

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }
}
