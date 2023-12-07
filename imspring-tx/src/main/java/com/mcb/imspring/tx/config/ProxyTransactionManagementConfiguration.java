package com.mcb.imspring.tx.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.tx.transaction.td.AnnotationTransactionAttributeSource;
import com.mcb.imspring.tx.transaction.tm.DataSourceTransactionManager;
import com.mcb.imspring.tx.advisor.TransactionAttributeSourceAdvisor;
import com.mcb.imspring.tx.advisor.TransactionInterceptor;
import com.mcb.imspring.tx.proxy.TransactionAutoProxyCreator;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;
import com.mcb.imspring.tx.transaction.tm.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ProxyTransactionManagementConfiguration {

    @Bean
    public TransactionAutoProxyCreator transactionAutoProxyCreator() {
        return new TransactionAutoProxyCreator();
    }

    @Bean
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionInterceptor transactionInterceptor(TransactionAttributeSource transactionAttributeSource, PlatformTransactionManager platformTransactionManager) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(platformTransactionManager);
        interceptor.setTransactionAttributeSource(transactionAttributeSource);
        return interceptor;
    }

    @Bean
    public TransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor(TransactionInterceptor transactionInterceptor) {
        TransactionAttributeSourceAdvisor advisor = new TransactionAttributeSourceAdvisor();
        advisor.setTransactionInterceptor(transactionInterceptor);
        return advisor;
    }
}
