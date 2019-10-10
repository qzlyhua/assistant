package com.shenbianys.assistant.service;

import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author Yang Hua
 */
public interface MongoService {
    <T> List<T> find(String env, Query query, Class<T> entityClass, String collectionName);

    <T> T insert(String env, T objectToSave, String collectionName);
}
