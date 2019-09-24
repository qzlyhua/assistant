package com.shenbianys.assisant.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Hua
 */
@Slf4j
public class EhcacheUtils {

    /**
     * 设置缓存对象
     *
     * @param cacheManager
     */
    public static void addCacheUser(CacheManager cacheManager, String openid) {
        Cache cache = cacheManager.getCache("dingDingUserCache");
        Set<String> users = cache.get("users", HashSet.class);
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(openid);
        cache.put("users", users);
        log.info("cache users:{}", users);
    }

    public static Set<String> getCacheUser(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("dingDingUserCache");
        Set<String> users = cache.get("users", HashSet.class);
        if (users == null) {
            return new HashSet<>();
        }
        log.info("cache users:{}", users);
        return users;
    }
}
