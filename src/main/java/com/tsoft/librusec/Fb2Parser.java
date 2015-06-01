package com.tsoft.librusec;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Fb2Parser {
    public Fb2Parser() { }

    public void parse(String fileName, Main.BookConsumer consumer) throws IOException {
        // file names in the zip are UTF-8
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileName), StandardCharsets.UTF_8)) {
            int fc = 0;
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                if (!entry.getName().endsWith(".fb2")) {
                    System.out.println("  Unsupported extension of " + entry.getName() + ", skipped");
                    continue;
                }

                BookTitle bookTitle = new BookTitle();
                bookTitle.zipFileName = new File(fileName).getName();
                bookTitle.fileName = entry.getName();

                byte[] buf = new byte[2048];
                int bufSize = zis.read(buf);

                String defaultCharsetName = "windows-1251";
                String text = new String(buf, 0, bufSize, Charset.forName(defaultCharsetName));

                // read the charset
                String charsetName = getValue(text, " encoding=\"", "\"");
                if (charsetName == null) {
                    charsetName = defaultCharsetName;
                    System.out.println("  Unknown encoding for " + entry.getName() + ", using " + defaultCharsetName);
                }

                if (!defaultCharsetName.equalsIgnoreCase(charsetName)) {
                    text = new String(buf, 0, bufSize, Charset.forName(charsetName));
                }

                String title = getValue(text, "<title-info>", "</title-info>");

                // try to read further (buf size = 2048 doesn't mean all 2048 bytes will be read at once)
                while (title == null) {
                    bufSize = zis.read(buf);
                    if (bufSize == -1) break;

                    text = text + new String(buf, 0, bufSize, Charset.forName(charsetName));
                    title = getValue(text, "<title-info>", "</title-info>");
                }
                if (title == null) {
                    System.out.println("  Undefined <title-info> for " + entry.getName() + ", skipped");
                    continue;
                }

                bookTitle.genre = getValue(title, "<genre>", "</genre>");
                bookTitle.title = getValue(title, "<book-title>", "</book-title>");
                bookTitle.lang = getValue(title, "<lang>", "</lang>");
                bookTitle.authorFirstName = getValue(title, "<first-name>", "</first-name>");
                bookTitle.authorLastName = getValue(title, "<last-name>", "</last-name>");
                bookTitle.authorMiddleName = getValue(title, "<middle-name>", "</middle-name>");

                consumer.accept(bookTitle);

                fc ++;
                if ((fc % 1000) == 0) System.out.print('.');
            }
        }
    }

    private String getValue(String text, String begin, String end) {
        int from = text.indexOf(begin);
        if (from == -1) return null;

        int to = text.indexOf(end, from + begin.length());
        if (to == -1) return null;

        return text.substring(from + begin.length(), to);
    }
}
