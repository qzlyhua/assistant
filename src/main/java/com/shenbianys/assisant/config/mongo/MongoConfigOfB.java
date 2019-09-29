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
public class MongoConfigOfB {
    @Value("${spring.data.mongodb.test.host}")
    String host;
    @Value("${spring.data.mongodb.test.port}")
    Integer port;
    @Value("${spring.data.mongodb.test.database}")
    String database;
    @Value("${spring.data.mongodb.test.username}")
    String username;
    @Value("${spring.data.mongodb.test.password}")
    String password;

    @Bean(name = "mongoTemplateB")
    public MongoTemplate getMongoTemplateB() {
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoCredential e = MongoCredential.createScramSha1Credential(username, "admin", password.toCharArray());
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(
                new MongoClient(serverAddress, e, MongoClientOptions.builder().build()), database);
        return new MongoTemplate(simpleMongoDbFactory);
    }
}
