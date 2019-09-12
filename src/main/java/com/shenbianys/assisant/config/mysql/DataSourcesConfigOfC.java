package com.shenbianys.assisant.config.mysql;

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
public class DataSourcesConfigOfC {
    @Value("${spring.datasource.testtjd.url}")
    String urlC;
    @Value("${spring.datasource.testtjd.username}")
    String usernameC;
    @Value("${spring.datasource.testtjd.password}")
    String passwordC;

    @Bean(name = "dataSourceC")
    public DataSource dataSourceA() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(urlC);
        dataSource.setUsername(usernameC);
        dataSource.setPassword(passwordC);
        return dataSource;
    }

    @Bean(name = "jdbcTemplateC")
    public JdbcTemplate jdbcTemplateC(@Qualifier("dataSourceC") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
