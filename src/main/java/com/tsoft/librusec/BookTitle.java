package com.tsoft.librusec;

import java.io.BufferedWriter;
import java.io.IOException;

public class BookTitle {
    public String fileName;
    public String authorFirstName;
    public String authorLastName;
    public String authorMiddleName;
    public String genre;
    public String title;
    public String lang;

    public static String getCsvFields() {
        return "lang,genre,title,author,file";
    }

    public void writeToCsv(BufferedWriter writer) throws IOException {
        writer.write(toCsv(lang));
        writer.write(',');
        writer.write(toCsv(genre));
        writer.write(',');
        writer.write(toCsv(title));
        writer.write(',');
        writer.write(toCsv(authorFirstName + " " + authorMiddleName + " " + authorLastName));
        writer.write(',');
        writer.write(toCsv(fileName));
        writer.write(',');
        writer.newLine();

        writer.flush();
    }

    private String toCsv(String value) {
        if (value == null) return "";

        // replace " with ""
        int n = 0;
        while ((n = value.indexOf('"', n)) != -1) {
            value = value.substring(0, n) + '"' + value.substring(n + 1);
            n = n + 1;
        }

        // if value contains comma(s), it should be in "
        n = value.indexOf(',');
        if (n == -1) n = value.indexOf(';');

        if (n != -1) value = '"' + value + '"';

        return value;
    }
}
