package com.shenbianys.assisant.config.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author Yang Hua
 */
@Configuration
public class MongoConfigOfA {
    @Value("${spring.data.mongodb.dev.host}")
    String host;
    @Value("${spring.data.mongodb.dev.port}")
    Integer port;
    @Value("${spring.data.mongodb.dev.database}")
    String database;
    @Value("${spring.data.mongodb.dev.username}")
    String username;
    @Value("${spring.data.mongodb.dev.password}")
    String password;

    @Primary
    @Bean(name = "mongoTemplateA")
    public MongoTemplate getMongoTemplateA() {
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoCredential e = MongoCredential.createScramSha1Credential(username, "admin", password.toCharArray());
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(new MongoClient(serverAddress, e, MongoClientOptions.builder().build()), database);
        return new MongoTemplate(simpleMongoDbFactory);
    }
}
