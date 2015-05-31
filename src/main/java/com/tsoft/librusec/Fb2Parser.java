package com.tsoft.librusec;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Fb2Parser {
    public Fb2Parser() { }

    public void parse(String fileName, Main.BookConsumer consumer) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileName), Charset.forName("cp1251"))) {
            String line;
            boolean isTitle = false;

            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".fb2")) {
                    BookTitle bookTitle = new BookTitle();
                    bookTitle.zipFileName = new File(fileName).getName();
                    bookTitle.fileName = entry.getName();

                    BufferedReader in = new BufferedReader(new InputStreamReader(zis));
                    while ((line = in.readLine()) != null) {
                        line = line.trim();
                        if (isTitle) {
                            if (line.startsWith("</title-info>")) break;

                            if (line.startsWith("<genre>")) bookTitle.genre = getValue(line, "genre");
                            if (line.startsWith("<book-title>")) bookTitle.title = getValue(line, "book-title");
                            if (line.startsWith("<lang>")) bookTitle.lang = getValue(line, "lang");
                            if (line.startsWith("<first-name>"))
                                bookTitle.authorFirstName = getValue(line, "first-name");
                            if (line.startsWith("<last-name>")) bookTitle.authorFirstName = getValue(line, "last-name");
                            if (line.startsWith("<middle-name>"))
                                bookTitle.authorFirstName = getValue(line, "middle-name");
                        } else {
                            isTitle = line.startsWith("<title-info>");
                        }
                    }

                    consumer.accept(bookTitle);
                } else System.out.println("Unknown extension, '" + fileName + "#" + entry.getName() + "' skipped");
            }
        }
    }

    private String getValue(String line, String attr) {
        int n1 = line.indexOf("<" + attr + ">");
        if (n1 == -1) return null;

        int n2 = line.indexOf("</" + attr + ">");
        if (n2 == -1) return null;

        return line.substring(n1 + attr.length() + 2, n2);
    }
}
