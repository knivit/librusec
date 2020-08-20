package com.tsoft.librusec;

import com.tsoft.librusec.dto.Library;
import com.tsoft.librusec.service.library.LibraryService;
import com.tsoft.librusec.service.writer.CsvLibraryWriter;
import com.tsoft.librusec.service.writer.HtmlLibraryWriter;
import com.tsoft.librusec.service.writer.LibraryWriter;

import java.util.Arrays;
import java.util.List;

public class Main {

    private final LibraryService libraryService = new LibraryService();
    
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1 || args[0].equalsIgnoreCase("-help")) {
            System.out.println("Usage: java -jar librusec.jar <Path to the library's folder>");
            System.exit(0);
        }

        new Main().parse(args[0]);
    }

    private void parse(String folder) throws Exception {
        System.out.println("Processing directory " + folder);
        libraryService.process(folder);

        System.out.println("Loading library " + folder);
        Library library = libraryService.deserializeFromFolder(folder);

        System.out.println("Generating index " + folder);
        List<LibraryWriter> libraryWriters = Arrays.asList(new CsvLibraryWriter(), new HtmlLibraryWriter());
        for (LibraryWriter libraryWriter : libraryWriters) {
            libraryWriter.open(folder);
            libraryWriter.process(library);
        }
    }
}
