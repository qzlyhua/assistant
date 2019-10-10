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
public class DataSourcesConfigOfD {
    @Value("${spring.datasource.pro.url}")
    String urlD;
    @Value("${spring.datasource.pro.username}")
    String usernameD;
    @Value("${spring.datasource.pro.password}")
    String passwordD;

    @Bean(name = "dataSourceD")
    public DataSource dataSourceA() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(urlD);
        dataSource.setUsername(usernameD);
        dataSource.setPassword(passwordD);
        return dataSource;
    }

    @Bean(name = "jdbcTemplateD")
    public JdbcTemplate jdbcTemplateD(@Qualifier("dataSourceD") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
