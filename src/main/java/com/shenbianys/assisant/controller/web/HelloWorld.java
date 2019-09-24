package com.shenbianys.assisant.controller.web;

import com.shenbianys.assisant.util.EhcacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author Yang Hua
 */
@RestController
public class HelloWorld {
    @Autowired
    CacheManager cacheManager;

    @RequestMapping(value = {"/hello/get"})
    public Set<String> get() {
        return EhcacheUtils.getCacheUser(cacheManager);
    }

    @RequestMapping(value = {"/hello/put/{id}"})
    public String put(@PathVariable String id) {
        EhcacheUtils.addCacheUser(cacheManager, id);
        return "ok";
    }
}
