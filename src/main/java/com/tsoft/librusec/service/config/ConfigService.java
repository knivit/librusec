package com.tsoft.librusec.service.config;

import com.tsoft.librusec.service.cache.CacheFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.tsoft.librusec.util.FileUtil.workingFolder;

@Slf4j
public class ConfigService {

    public Config getConfig() {
        Cache cache = CacheFactory.getConfigCache();
        return cache.get("config", this::loadConfig);
    }

    private Config loadConfig() {
        try (Reader reader = Files.newBufferedReader(getConfigFolder())) {
            Properties props = new Properties();
            props.load(reader);
            Config config = Config.from(props);

            log.info("Loaded config: {}", config);
            return config;
        } catch (Exception ex) {
            return null;
        }
    }

    public void saveConfig(Config config) {
        Properties props = new Properties();
        props.put("version", Config.VERSION);
        props.put("booksFolder", config.getBooksFolder());
        props.put("cacheFolder", config.getCacheFolder());
        props.put("systemFolder", config.getSystemFolder());

        try (Writer writer = Files.newBufferedWriter(getConfigFolder())) {
            props.store(writer, "LibRusEc configuration file");
            Cache cache = CacheFactory.getConfigCache();
            cache.clear();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        log.info("Config saved: {}", config);
    }

    private Path getConfigFolder() {
        return Path.of(workingFolder().toString(), "config.txt");
    }
}
