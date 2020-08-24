package com.tsoft.librusec.service.library;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.config.ConfigService;
import com.tsoft.librusec.service.writer.CsvLibraryWriter;
import com.tsoft.librusec.service.writer.HtmlLibraryWriter;
import com.tsoft.librusec.service.writer.LibraryWriter;
import com.tsoft.librusec.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class LibraryReferenceGenerator {

    private final ConfigService configService = new ConfigService();
    private final LibraryService libraryService = new LibraryService();

    public Config prepareConfig(String booksFolder) {
        Config config = configService.loadConfig();
        if (config != null && config.getBooksFolder() != null &&
            Files.exists(Path.of(config.getBooksFolder())) && config.getBooksFolder().equals(booksFolder)) {
            return config;
        }

        if (booksFolder == null || !Files.exists(Path.of(booksFolder))) {
            log.error("Books folder= '{}' must not be null and must exists", booksFolder);
            return null;
        }

        config = Config.builder()
            .booksFolder(booksFolder)
            .cacheFolder(booksFolder + "/.cache")
            .libraryFolder(booksFolder + "/.library")
            .htmlFolder(booksFolder + "/.html")
            .csvFolder(booksFolder + "/.csv")
            .build();

        FileUtil.createDirectories(config.getCacheFolder());
        FileUtil.createDirectories(config.getLibraryFolder());
        FileUtil.createDirectories(config.getHtmlFolder());
        FileUtil.createDirectories(config.getCsvFolder());

        configService.saveConfig(config);
        return config;
    }

    public void generate(Config config) throws Exception {
        libraryService.process(config);

        Library library = libraryService.load(config);

        List<LibraryWriter> libraryWriters = Arrays.asList(new CsvLibraryWriter(), new HtmlLibraryWriter());
        for (LibraryWriter libraryWriter : libraryWriters) {
            libraryWriter.process(config, library);
        }
    }
}
