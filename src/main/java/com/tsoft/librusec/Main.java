package com.tsoft.librusec;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
        System.out.println("Processing directory " + folder);
        String outputFileName = folder + "/index.csv";

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8))) {
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
        File[] files = root.listFiles((dir, name) -> name.endsWith(".zip"));
        if (files == null) return;

        for (int i = 0; i < files.length; i ++) {
            System.out.print("File " + (i + 1) + " of " + files.length + " " + files[i].getName() + ": ");
            long millis = System.currentTimeMillis();
            Fb2Parser parser = new Fb2Parser();
            parser.parse(files[i].getAbsolutePath(), consumer);
            System.out.println(" done in " + (System.currentTimeMillis() - millis)/1000 + " sec");
        }
    }
}
