package com.shenbianys.assistant.config.mysql;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Yang Hua
 */
@Configuration
public class DataSourcesConfigOfB {
    @Value("${spring.datasource.test.url}")
    String urlB;
    @Value("${spring.datasource.test.username}")
    String usernameB;
    @Value("${spring.datasource.test.password}")
    String passwordB;

    @Bean(name = "dataSourceB")
    public DataSource dataSourceA() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(urlB);
        dataSource.setUsername(usernameB);
        dataSource.setPassword(passwordB);
        return dataSource;
    }

    @Bean(name = "jdbcTemplateB")
    public JdbcTemplate jdbcTemplateB(@Qualifier("dataSourceB") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
