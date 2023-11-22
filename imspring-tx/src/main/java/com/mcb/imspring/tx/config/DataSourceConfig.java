package com.mcb.imspring.tx.config;

import com.mcb.imspring.core.annotation.Bean;
import com.mcb.imspring.core.annotation.Configuration;
import com.mcb.imspring.core.annotation.Value;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${imspring.datasource.url}")
    private String url;

    @Value("${imspring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${imspring.datasource.username}")
    private String username;

    @Value("${imspring.datasource.password}")
    private String password;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setDriverClassName(driverClassName);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
