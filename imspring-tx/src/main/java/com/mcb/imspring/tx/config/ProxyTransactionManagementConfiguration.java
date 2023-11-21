package com.mcb.imspring.tx.config;

import com.mcb.imspring.aop.AnnotationAwareAspectJAutoProxyCreator;
import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.tx.AnnotationTransactionAttributeSource;
import com.mcb.imspring.tx.DataSourceTransactionManager;
import com.mcb.imspring.tx.TransactionInterceptor;
import com.mcb.imspring.tx.TransactionAttributeSourceAdvisor;
import com.mcb.imspring.tx.proxy.TransactionAutoProxyCreator;
import com.mcb.imspring.tx.transaction.td.TransactionAttributeSource;

import javax.sql.DataSource;

@Configuration
public class ProxyTransactionManagementConfiguration {

    @Bean
    public TransactionAutoProxyCreator transactionAutoProxyCreator() {
        return new TransactionAutoProxyCreator();
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        // TODO
        return null;
    }

    @Bean
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionInterceptor transactionInterceptor(TransactionAttributeSource transactionAttributeSource, DataSourceTransactionManager dataSourceTransactionManager) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(dataSourceTransactionManager);
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
