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
        writeHeader(writer);

        writeByAuthorsIndex(writer, library);
        writeByYearIndex(writer, library);

        writeFooter(writer);
    }

    private void writeHeader(Writer writer) throws IOException {
        writer.write(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">" +
            "<title>LibRuSec</title>\n" +
            "<link rel='stylesheet' href='default.css'/>\n" +
            "</head>\n" +
            "<body>");
    }

    private void writeFooter(Writer writer) throws IOException {
        writer.write("</body>\n</html>\n");
    }

    private void writeByAuthorsIndex(Writer writer, Library library) throws IOException {
        ArrayList<ByAuthorGroup> groups = libraryService.groupByAuthor(library);

        writer.write("\n<h2>По авторам:</h2>\n");
        for (ByAuthorGroup group : groups) {
            String name = (group.letter == 0 ? "0-9, eng" : String.valueOf(group.letter));
            writer.write("<a href='a_" + Integer.toHexString(group.letter) + "_0.html'>" + name + " (" + group.count + ")</a>");
            writer.write("<br>\n");
        }
    }

    private void writeByYearIndex(Writer writer, Library library) throws IOException {
        ArrayList<ByYearGroup> groups = libraryService.groupByYear(library);

        writer.write("\n<h2>По году:</h2>\n");
        for (ByYearGroup group : groups) {
            String name = group.year;
            writer.write("<a href='y_" + group.year + "_0.html'>" + name + " (" + group.count + ")</a>");
            writer.write("<br>\n");
        }
    }

}
