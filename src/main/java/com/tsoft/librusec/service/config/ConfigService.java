package com.tsoft.librusec.service.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.tsoft.librusec.util.FileUtil.workingFolder;

public class ConfigService {

    private static final CacheManager cacheManager = new ConcurrentMapCacheManager();
    private static final Cache cache = cacheManager.getCache("default");

    public Config getConfig() {
        return cache.get("config", () -> loadConfig());
    }

    private Config loadConfig() {
        try (Reader reader = Files.newBufferedReader(getConfigFolder())) {
            Properties props = new Properties();
            props.load(reader);
            return Config.from(props);
        } catch (Exception ex) {
            return null;
        }
    }

    public void saveConfig(Config config) {
        Properties props = new Properties();
        props.put("version", Config.VERSION);
        props.put("booksFolder", config.getBooksFolder());
        props.put("cacheFolder", config.getCacheFolder());
        props.put("libraryFolder", config.getLibraryFolder());
        props.put("htmlFolder", config.getHtmlFolder());
        props.put("csvFolder", config.getCsvFolder());

        try (Writer writer = Files.newBufferedWriter(getConfigFolder())) {
            props.store(writer, "LibRusEc configuration file");
            cache.clear();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Path getConfigFolder() {
        return Path.of(workingFolder().toString(), "config.txt");
    }
}
