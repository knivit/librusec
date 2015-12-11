package com.tsoft.librusec.consumer;

import com.tsoft.librusec.BookTitle;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class CsvConsumer implements Consumer {
    private BufferedWriter writer;

    @Override
    public void open(String outputFolder) throws IOException {
        String outputFileName = outputFolder + "/index.csv";
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));

        writer.write("lang,genre,date,title,author,zip,file");
        writer.newLine();
    }

    @Override
    public void accept(BookTitle bookTitle) throws IOException {
        writer.write(toCsv(bookTitle.lang));
        writer.write(',');
        writer.write(toCsv(bookTitle.genre));
        writer.write(',');
        writer.write(toCsv(bookTitle.date));
        writer.write(',');
        writer.write(toCsv(bookTitle.title));
        writer.write(',');
        writer.write(toCsv(bookTitle.authors));
        writer.write(',');
        writer.write(toCsv(bookTitle.zipFileName));
        writer.write(',');
        writer.write(toCsv(bookTitle.fileName));
        writer.newLine();

        writer.flush();
    }

    @Override
    public void close() throws IOException {
        if (writer != null) writer.close();
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
