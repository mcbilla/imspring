package com.mcb.imspring.tx.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.tx.DataSourceTransactionManager;
import com.mcb.imspring.tx.TransactionInterceptor;
import com.mcb.imspring.tx.transaction.td.AnnotationTransactionAttributeSource;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;

@Configuration
public class ProxyTransactionManagementConfiguration {
    @Bean
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    @Bean
    public TransactionInterceptor transactionInterceptor() {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(new DataSourceTransactionManager());
        interceptor.setTransactionAttributeSource(transactionAttributeSource());
        return interceptor;
    }
}
