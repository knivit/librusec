package com.tsoft.librusec.service.generator;

import com.tsoft.librusec.service.library.Library;
import com.tsoft.librusec.service.library.LibraryService;
import com.tsoft.librusec.service.library.group.ByAuthorGroup;
import com.tsoft.librusec.service.library.group.ByYearGroup;

import java.io.*;
import java.util.ArrayList;

class IndexPageGenerator {

    private final LibraryService libraryService = new LibraryService();

    public void generate(Writer writer, Library library) throws IOException {
        writeByAuthorsIndex(writer, library);
        writeByYearIndex(writer, library);
    }

    private void writeByAuthorsIndex(Writer writer, Library library) throws IOException {
        ArrayList<ByAuthorGroup> groups = libraryService.groupByAuthor(library);

        writer.write("\n<h2>By authors:</h2>\n");
        for (ByAuthorGroup group : groups) {
            String name = (group.letter == 0 ? "0-9, eng" : String.valueOf(group.letter));
            writer.write("<a href='a?f=" + Integer.toHexString(group.letter) + "'>" + name + " (" + group.count + ")</a>");
            writer.write("<br>\n");
        }
    }

    private void writeByYearIndex(Writer writer, Library library) throws IOException {
        ArrayList<ByYearGroup> groups = libraryService.groupByYear(library);

        writer.write("\n<h2>By year:</h2>\n");
        for (ByYearGroup group : groups) {
            String name = group.year;
            writer.write("<a href='y?f=" + group.year + "'>" + name + " (" + group.count + ")</a>");
            writer.write("<br>\n");
        }
    }
}
