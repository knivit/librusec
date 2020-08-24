package com.tsoft.librusec.util;

import com.tsoft.librusec.dto.Config;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class FileUtil {

    private FileUtil() { }

    public static Path workingFolder() {
        try {
            Path workingFolder = Path.of(System.getProperty("user.home") + "/.librusec");
            Files.createDirectories(workingFolder);
            return workingFolder;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static Config loadConfig() {
        try (Reader reader = Files.newBufferedReader(Path.of(workingFolder().toString(), "config.txt"))) {
            Properties props = new Properties();
            props.load(reader);
            return Config.from(props);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void saveConfig(Config config) {
        Properties props = new Properties();
        props.put("version", Config.VERSION);
        props.put("booksFolder", config.getBooksFolder());
        props.put("cacheFolder", config.getCacheFolder());

        try (Writer writer = Files.newBufferedWriter(Path.of(workingFolder().toString(), "config.txt"))) {
            props.store(writer, "LibRusEc configuration file");
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void createDirectories(String folderName) {
        try {
            Files.createDirectories(Path.of(folderName));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static String changeExtension(String fileName, String ext) {
        int n = fileName.lastIndexOf('.');
        return ((n == -1) ? fileName : fileName.substring(0, n)) + ext;
    }
}
