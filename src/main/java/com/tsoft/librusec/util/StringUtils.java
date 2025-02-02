package com.tsoft.librusec.util;

public final class StringUtils {

    private StringUtils() { }

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }

        for (int i = 0; i < str.length(); i ++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                continue;
            }
            return false;
        }

        return true;
    }

    public static String trim(String str) {
        if (str == null) {
            return str;
        }

        int from = -1;
        for (int i = 0; i < str.length(); i ++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                continue;
            }
            from = i;
            break;
        }

        if (from == -1) {
            return null;
        }

        int to = -1;
        for (int i = str.length() - 1; i >= 0; i --) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                continue;
            }
            to = i;
            break;
        }

        if (from <= 0 && to >= str.length() - 1) {
            return str;
        }

        return str.substring(from, to + 1);
    }
}
