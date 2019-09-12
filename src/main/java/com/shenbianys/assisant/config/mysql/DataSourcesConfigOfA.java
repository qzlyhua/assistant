package com.shenbianys.assisant.config.mysql;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Yang Hua
 */
@Configuration
public class DataSourcesConfigOfA {
    @Value("${spring.datasource.dev.url}")
    String urlA;
    @Value("${spring.datasource.dev.username}")
    String usernameA;
    @Value("${spring.datasource.dev.password}")
    String passwordA;

    @Bean(name = "dataSourceA")
    @Primary
    public DataSource dataSourceA() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(urlA);
        dataSource.setUsername(usernameA);
        dataSource.setPassword(passwordA);
        return dataSource;
    }

    @Bean(name = "jdbcTemplateA")
    public JdbcTemplate jdbcTemplateA(@Qualifier("dataSourceA") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
