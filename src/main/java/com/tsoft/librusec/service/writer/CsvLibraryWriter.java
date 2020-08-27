package com.tsoft.librusec.service.writer;

import com.tsoft.librusec.service.library.Book;
import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.library.Library;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CsvLibraryWriter implements LibraryWriter {

    @Override
    public void process(Config config, Library library) throws Exception {
        log.info("Generating CSV index {}", config.getCsvFolder());

        try (BufferedWriter writer = prepareWriter(config.getCsvFolder())) {
            for (Book book : library.getBooks()) {
                writer.write(toCsv(book.lang));
                writer.write(',');
                writer.write(toCsv(book.genre));
                writer.write(',');
                writer.write(toCsv(book.date));
                writer.write(',');
                writer.write(toCsv(book.title));
                writer.write(',');
                writer.write(toCsv(book.authors));
                writer.write(',');
                writer.write(toCsv(book.zipFileName));
                writer.write(',');
                writer.write(toCsv(book.fileName));
                writer.newLine();
            }
        }
    }

    private BufferedWriter prepareWriter(String outputFolder) throws IOException {
        String outputFileName = outputFolder + "/index.csv";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));

        writer.write("lang,genre,date,title,author,zip,file");
        writer.newLine();

        return writer;
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
