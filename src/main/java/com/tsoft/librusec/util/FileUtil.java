package com.tsoft.librusec.util;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {

    private FileUtil() { }

    public static Path workingFolder() {
        try {
            Path workingFolder = Path.of(System.getProperty("user.home"), ".config", ".librusec");
            Files.createDirectories(workingFolder);
            return workingFolder;
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
