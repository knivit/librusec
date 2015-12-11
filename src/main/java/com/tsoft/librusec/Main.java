package com.tsoft.librusec;

import com.tsoft.librusec.consumer.BatchConsumersManager;
import com.tsoft.librusec.consumer.Consumer;
import com.tsoft.librusec.consumer.CsvConsumer;
import com.tsoft.librusec.consumer.HtmlConsumer;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {
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

        BatchConsumersManager batchConsumersManager = new BatchConsumersManager();
        batchConsumersManager.registerConsumer(new HtmlConsumer());
        List<Consumer> consumers = Arrays.asList(new CsvConsumer(), batchConsumersManager);
        try {
            for (Consumer consumer : consumers) consumer.open(folder);

            int count = walk(folder, consumers);

            System.out.println(count + " file(s) processed");
            System.out.println("See results in " + folder);
        } finally {
            for (Consumer consumer : consumers) consumer.close();
        }
    }

    private static int walk(String path, List<Consumer> consumers) throws IOException {
        File root = new File(path);
        File[] files = root.listFiles((dir, name) -> name.endsWith(".zip"));
        if (files == null) return 0;

        int totalCount = 0;
        for (int i = 0; i < files.length; i ++) {
            System.out.print("File " + (i + 1) + " of " + files.length + " " + files[i].getName() + ": ");
            long millis = System.currentTimeMillis();
            Fb2Parser parser = new Fb2Parser();
            int count = parser.parse(files[i].getAbsolutePath(), consumers);
            System.out.println(" done in " + (System.currentTimeMillis() - millis)/1000 + " sec, " + count + " book(s) found");

            totalCount += count;
        }
        return totalCount;
    }
}
