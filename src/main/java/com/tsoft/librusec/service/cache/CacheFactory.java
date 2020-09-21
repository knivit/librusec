package com.tsoft.librusec.service.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

public class CacheFactory {
    private static final CacheManager cacheManager = new ConcurrentMapCacheManager();

    private static final Cache configCache = cacheManager.getCache("config");
    private static final Cache libraryCache = cacheManager.getCache("library");

    private CacheFactory() { }

    public static Cache getConfigCache() {
        return configCache;
    }

    public static Cache getLibraryCache() {
        return libraryCache;
    }
}
