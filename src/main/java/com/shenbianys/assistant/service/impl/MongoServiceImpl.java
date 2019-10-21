package com.shenbianys.assistant.service.impl;

import com.shenbianys.assistant.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yang Hua
 */
@Service
public class MongoServiceImpl implements MongoService {
    @Autowired
    @Qualifier("mongoTemplateA")
    MongoTemplate mongoTemplateDev;

    @Autowired
    @Qualifier("mongoTemplateB")
    MongoTemplate mongoTemplateTest;

    @Autowired
    @Qualifier("mongoTemplateC")
    MongoTemplate mongoTemplateTesttjd;

    @Autowired
    @Qualifier("mongoTemplateD")
    MongoTemplate mongoTemplatePro;

    @Override
    public MongoTemplate getMongoTemplateByEnv(String env) {
        if ("test".equals(env)) {
            return mongoTemplateTest;
        } else if ("testtjd".equals(env)) {
            return mongoTemplateTesttjd;
        } else if ("pro".equals(env)) {
            return mongoTemplatePro;
        } else {
            return mongoTemplateDev;
        }
    }

    @Override
    public <T> List<T> find(String env, Query query, Class<T> entityClass, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplateByEnv(env);
        if (mongoTemplate != null) {
            return mongoTemplate.find(query, entityClass, collectionName);
        } else {
            return null;
        }
    }

    @Override
    public long count(String env, Query query, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplateByEnv(env);
        if (mongoTemplate != null) {
            return mongoTemplate.count(query, collectionName);
        } else {
            return 0;
        }
    }

    @Override
    public <T> T insert(String env, T objectToSave, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplateByEnv(env);
        if (mongoTemplate != null) {
            return mongoTemplate.insert(objectToSave, collectionName);
        } else {
            return null;
        }
    }
}
