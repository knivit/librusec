package com.tsoft.librusec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    static class BookConsumer {
        public int counter = 0;

        private BufferedWriter writer;

        BookConsumer(BufferedWriter writer) {
            this.writer = writer;
        }

        public void accept(BookTitle bookTitle) throws IOException {
            bookTitle.writeToCsv(writer);
            counter ++;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 1 || args[0].equalsIgnoreCase("-help")) {
            System.out.println("Usage: java -jar librusec.jar <Path to the library's folder>");
            System.exit(0);
        }

        Main main = new Main();
        main.parse(args[0]);
    }

    public void parse(String folder) throws IOException{
        System.out.println("Processing directory: " + folder);
        String outputFileName = folder + "/index.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write(BookTitle.getCsvFields());
            writer.newLine();

            BookConsumer bookConsumer = new BookConsumer(writer);
            walk(folder, bookConsumer);

            System.out.println(bookConsumer.counter + " file(s) processed");
            System.out.println("See results in " + outputFileName);
        }
    }

    private static void walk(String path, BookConsumer consumer) throws IOException {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) walk(f.getAbsolutePath(), consumer);
            else if (!f.getAbsolutePath().endsWith(".fb2")) continue;
            else {
                Fb2Parser parser = new Fb2Parser();
                BookTitle bookTitle = parser.parse(f.getAbsolutePath());
                consumer.accept(bookTitle);
            }
        }
    }
}
