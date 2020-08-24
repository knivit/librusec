package com.tsoft.librusec.service.download;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.config.ConfigService;
import com.tsoft.librusec.util.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DownloadService {

    private final ConfigService configService = new ConfigService();

    public DownloadResult download(String zipFileName, String bookFileName) {
        Config config = configService.loadConfig();
        if (config == null) {
            return DownloadResult.builder()
                .status(DownloadResult.Status.FAIL)
                .errorMessage("Config is null")
                .build();
        }

        try (ZipFile file = new ZipFile(zipFileName, StandardCharsets.UTF_8)) {
            Enumeration<? extends ZipEntry> entries = file.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().equals(bookFileName)) {
                    String cacheFolder = config.getCacheFolder() + "/" + FileUtil.changeExtension(zipFileName, "");
                    FileUtil.createDirectories(cacheFolder);

                    Path resultFile = Path.of(cacheFolder, bookFileName);
                    Files.copy(file.getInputStream(entry), resultFile, StandardCopyOption.REPLACE_EXISTING);
                    return DownloadResult.builder()
                        .fileName(resultFile)
                        .status(DownloadResult.Status.SUCCESS)
                        .build();
                }
            }

            return DownloadResult.builder()
                .status(DownloadResult.Status.NOT_FOUND)
                .build();
        } catch (Exception ex) {
            return DownloadResult.builder()
                .status(DownloadResult.Status.FAIL)
                .errorMessage(ex.getMessage())
                .build();
        }
    }
}
