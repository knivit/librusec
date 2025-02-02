package com.tsoft.librusec.service.cache;

import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

public class CacheFactory {

    private static final CacheManager cacheManager = new ConcurrentMapCacheManager();

    @Getter
    private static final Cache configCache = cacheManager.getCache("config");

    @Getter
    private static final Cache libraryCache = cacheManager.getCache("library");

    private CacheFactory() { }

}
