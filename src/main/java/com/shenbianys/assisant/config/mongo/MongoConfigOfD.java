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
public class MongoConfigOfD {
    @Value("${spring.data.mongodb.pro.host}")
    String host;
    @Value("${spring.data.mongodb.pro.port}")
    Integer port;
    @Value("${spring.data.mongodb.pro.database}")
    String database;
    @Value("${spring.data.mongodb.pro.username}")
    String username;
    @Value("${spring.data.mongodb.pro.password}")
    String password;

    @Primary
    @Bean(name = "mongoTemplateD")
    public MongoTemplate getMongoTemplateD() {
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoCredential e = MongoCredential.createScramSha1Credential(username, "admin", password.toCharArray());
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(new MongoClient(serverAddress, e, MongoClientOptions.builder().build()), database);
        return new MongoTemplate(simpleMongoDbFactory);
    }
}
