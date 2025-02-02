package com.tsoft.librusec.service.library;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.config.ConfigService;
import com.tsoft.librusec.service.writer.csv.CsvLibraryWriter;
import com.tsoft.librusec.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class LibraryReferenceGenerator {

    private final ConfigService configService = new ConfigService();
    private final LibraryService libraryService = new LibraryService();

    public Config prepareConfig(String booksFolder) {
        Config config = configService.getConfig();
        if (config != null && config.getBooksFolder() != null &&
            Files.exists(Path.of(config.getBooksFolder())) && config.getBooksFolder().equals(booksFolder)) {
            return config;
        }

        if (booksFolder == null || !Files.exists(Path.of(booksFolder))) {
            log.error("The books folder = '{}' doesn't exist", booksFolder);
            return null;
        }

        config = Config.builder()
            .booksFolder(booksFolder)
            .cacheFolder(booksFolder + "/.cache")
            .systemFolder(booksFolder + "/.system")
            .build();

        FileUtil.createDirectories(config.getCacheFolder());
        FileUtil.createDirectories(config.getSystemFolder());

        configService.saveConfig(config);
        return config;
    }

    public void generate(Config config) throws Exception {
        File[] nonProcessedFiles = findNonProcessedFiles(config);
        if (nonProcessedFiles == null || nonProcessedFiles.length == 0) {
            log.info("Non-processed zip files in {} not found, processing skipped", config.getBooksFolder());
            return;
        }

        libraryService.process(config, nonProcessedFiles);

        Library library = libraryService.getLibrary();
        CsvLibraryWriter.process(config, library);
        //HtmlLibraryWriter.process(config, library);
    }

    private File[] findNonProcessedFiles(Config config) {
        File root = new File(config.getBooksFolder());
        return root.listFiles((dir, name) -> (name.endsWith(".zip") || name.endsWith(".fb2")) &&
            !Files.exists(Path.of(config.getSystemFolder(), FileUtil.changeExtension(name, ".ser"))));
    }
}
