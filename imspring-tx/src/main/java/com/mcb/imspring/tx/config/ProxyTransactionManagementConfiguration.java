package com.mcb.imspring.tx.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.tx.DataSourceTransactionManager;
import com.mcb.imspring.tx.TransactionInterceptor;

@Configuration
public class ProxyTransactionManagementConfiguration {

    @Bean
    public TransactionInterceptor transactionInterceptor() {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(new DataSourceTransactionManager());
        return interceptor;
    }
}
