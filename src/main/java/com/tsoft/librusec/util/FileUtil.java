package com.tsoft.librusec.util;

public final class FileUtil {

    private FileUtil() { }

    public static String changeExtension(String fileName, String ext) {
        int n = fileName.lastIndexOf('.');
        return ((n == -1) ? fileName : fileName.substring(0, n)) + ext;
    }
}
