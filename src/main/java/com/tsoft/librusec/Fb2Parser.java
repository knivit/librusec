package com.tsoft.librusec;

import java.io.*;

public class Fb2Parser {
    public Fb2Parser() { }

    public BookTitle parse(String fileName) throws IOException {
        BookTitle bookTitle = new BookTitle();
        bookTitle.fileName = fileName;

        try (BufferedReader bis = new BufferedReader(new FileReader(fileName, ""))) {
            String line;
            boolean isTitle = false;
            while ((line = bis.readLine()) != null) {
                line = line.trim();
                if (isTitle) {
                    if (line.startsWith("</title-info>")) break;

                    if (line.startsWith("<genre>")) bookTitle.genre = getValue(line, "genre");
                    if (line.startsWith("<book-title>")) bookTitle.title = getValue(line, "book-title");
                    if (line.startsWith("<lang>")) bookTitle.lang = getValue(line, "lang");
                    if (line.startsWith("<first-name>")) bookTitle.authorFirstName = getValue(line, "first-name");
                    if (line.startsWith("<last-name>")) bookTitle.authorFirstName = getValue(line, "last-name");
                    if (line.startsWith("<middle-name>")) bookTitle.authorFirstName = getValue(line, "middle-name");
                } else {
                    isTitle = line.startsWith("<title-info>");
                }
            }
        }
        return bookTitle;
    }

    private String getValue(String line, String attr) {
        int n1 = line.indexOf("<" + attr + ">");
        if (n1 == -1) return null;

        int n2 = line.indexOf("</" + attr + ">");
        if (n2 == -1) return null;

        return line.substring(n1 + attr.length() + 2, n2);
    }
}
