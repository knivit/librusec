package com.tsoft.librusec;

import com.tsoft.librusec.service.parser.Fb2Parser;
import com.tsoft.librusec.service.writer.CsvLibraryWriter;
import com.tsoft.librusec.service.writer.HtmlLibraryWriter;
import com.tsoft.librusec.service.writer.LibraryWriter;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1 || args[0].equalsIgnoreCase("-help")) {
            System.out.println("Usage: java -jar librusec.jar <Path to the library's folder>");
            System.exit(0);
        }

        Main main = new Main();
        main.parse(args[0]);
    }

    public void parse(String folder) throws Exception{
        System.out.println("Processing directory " + folder);

        List<LibraryWriter> libraryWriters = Arrays.asList(new CsvLibraryWriter(), new HtmlLibraryWriter());

        for (LibraryWriter libraryWriter : libraryWriters) {
            libraryWriter.open(folder);
            process(folder, libraryWriters);
            libraryWriter.close();
        }
    }

    private void process(String path, List<LibraryWriter> libraryWriters) throws IOException {
        File root = new File(path);
        File[] files = root.listFiles((dir, name) -> name.endsWith(".zip"));
        if (files == null) {
            System.out.println("Zip files in " + path + " not found");
            return;
        }

        int totalCount = 0;
        long totalTime = 0;
        for (int i = 0; i < files.length; i ++) {
            System.out.print("File " + (i + 1) + " of " + files.length + " " + files[i].getName() + ": ");

            long millis = System.currentTimeMillis();
            Fb2Parser parser = new Fb2Parser();
            int count = parser.parse(files[i].getAbsolutePath(), libraryWriters);
            long time = (System.currentTimeMillis() - millis)/1000;
            System.out.println(" done in " + time + " sec, " + count + " book(s) found");

            totalCount += count;
            totalTime += time;
        }

        System.out.println(totalCount + " file(s) processed in " + totalTime + " sec");
        System.out.println("See results in " + path);
    }
}
