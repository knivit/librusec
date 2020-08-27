package com.tsoft.librusec.service.download;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.config.ConfigService;
import com.tsoft.librusec.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class DownloadService {

    private final ConfigService configService = new ConfigService();

    public DownloadResult download(String zipFileName, String bookFileName) {
        log.info("Downloading {}#{}", zipFileName, bookFileName);

        Config config = configService.getConfig();
        if (config == null) {
            return DownloadResult.builder()
                .status(DownloadResult.Status.FAIL)
                .errorMessage("Config is null")
                .build();
        }

        DownloadResult cached = findCachedFile(config, zipFileName, bookFileName);
        if (cached != null) {
            log.info("Found cached file {}#{}, {}", zipFileName, bookFileName, cached);
            return cached;
        }

        Instant now = Instant.now();
        File zipFile = Path.of(config.getBooksFolder(), zipFileName).toFile();
        DownloadResult extracted = extractFile(config, zipFile, bookFileName);
        log.info("Extracting {}#{} done, {}, took {}", zipFileName, bookFileName, extracted, Duration.between(now, Instant.now()));

        return extracted;
    }

    private String getZipFileCacheFolder(Config config, String zipFileName) {
        return config.getCacheFolder() + "/" + FileUtil.deleteExtension(zipFileName);
    }

    private DownloadResult findCachedFile(Config config, String zipFileName, String bookFileName) {
        String cacheFolder = getZipFileCacheFolder(config, zipFileName);
        Path cachedFile = Path.of(cacheFolder, bookFileName);

        if (Files.exists(cachedFile)) {
            return DownloadResult.builder()
                .fileName(cachedFile)
                .status(DownloadResult.Status.SUCCESS)
                .build();
        }

        return null;
    }

    private DownloadResult extractFile(Config config, File zipFile, String fileName) {
        try (ZipFile file = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            return file.stream()
                .filter(e -> e.getName().equals(fileName))
                .findFirst()
                .map(e -> doExtractFile(config, file, e))
                .orElse(fileNotFound());
        } catch (Exception ex) {
            return exceptionOccurred(ex, zipFile.getName(), fileName);
        }
    }

    private DownloadResult doExtractFile(Config config, ZipFile file, ZipEntry entry) {
        try {
            String zipFileName = Path.of(file.getName()).getFileName().toString();
            String cacheFolder = getZipFileCacheFolder(config, zipFileName);
            FileUtil.createDirectories(cacheFolder);

            Path extractedFile = Path.of(cacheFolder, entry.getName());
            Files.copy(file.getInputStream(entry), extractedFile, StandardCopyOption.REPLACE_EXISTING);

            return DownloadResult.builder()
                .fileName(extractedFile)
                .status(DownloadResult.Status.SUCCESS)
                .build();
        } catch (Exception ex) {
            return exceptionOccurred(ex, file.getName(), entry.getName());
        }
    }

    private DownloadResult fileNotFound() {
        return DownloadResult.builder()
            .status(DownloadResult.Status.NOT_FOUND)
            .build();
    }

    private DownloadResult exceptionOccurred(Exception ex, String zipFileName, String entryName) {
        log.error("Error extracting {}#{}", zipFileName, entryName, ex);

        return DownloadResult.builder()
            .status(DownloadResult.Status.FAIL)
            .errorMessage(ex.getMessage())
            .build();
    }
}
