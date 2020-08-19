package com.tsoft.librusec.service.writer;

import com.tsoft.librusec.dto.Book;
import com.tsoft.librusec.dto.Library;
import com.tsoft.librusec.dto.Section;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class HtmlLibraryWriter implements LibraryWriter {

    private String outputFolder;
    private Library library = new Library();

    @Override
    public void open(String folderName) throws Exception {
        outputFolder = folderName + "/html";
        Files.createDirectories(Paths.get(outputFolder));

        for (String fileName : new String[] { "code.js", "default.css", "zip.js", "z-worker.js", "inflate.js" }) {
            Path src = Paths.get(getClass().getResource("/" + fileName).toURI());
            Path dst = Paths.get(outputFolder, fileName);
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void accept(Book book) {
        library.addBook(book);
    }

    @Override
    public void close() throws IOException {
        ArrayList<Section> sections = library.getSections();

        String outputFileName = outputFolder + "/index.html";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));
        writer.write(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">" +
            "<title>LibRuSec</title>\n" +
            "<link rel='stylesheet' href='default.css'/>\n" +
            "</head>\n" +
            "<body>");

        // by authors
        writer.write("\n<h2>\u041f\u043e \u0430\u0432\u0442\u043e\u0440\u0430\u043c:</h2>\n");
        for (Section section : sections) {
            String name = (section.letter == 0 ? "0-9, eng" : String.valueOf(section.letter));
            writer.write("<a href='a_" + Integer.toHexString(section.letter) + "_0.html'>" + name + " (" + section.count + ")</a>");
            writer.write("<br>\n");
        }
        writer.write("</body>\n</html>\n");
        writer.close();

        int pageSize = 500;
        for (Section section : sections) {
            for (int page = 0; page * pageSize < section.count; page++) {
                outputFileName = outputFolder + "/a_" + Integer.toHexString(section.letter) + "_" + page + ".html";
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));

                String name = (section.letter == 0 ? "0-9, eng" : String.valueOf(section.letter));
                writeHtmlHeader(writer, name);

                writeTitle(writer, name);

                String firstPage = "%s", prevPage = "%s";
                if (page > 0) {
                    firstPage = "<a href='a_" + Integer.toHexString(section.letter) + "_0.html'>%s [1]</a>";
                    prevPage = "<a href='a_" + Integer.toHexString(section.letter) + "_" + (page - 1) + ".html'>%s [" + page + "]</a>";
                }

                String lastPage = "%s", nextPage = "%s";
                int n = (section.count / pageSize);
                if (page < n) {
                    lastPage = "<a href='a_" + Integer.toHexString(section.letter) + "_" + n + ".html'>%s [" + (n + 1) + "]</a>";
                    nextPage = "<a href='a_" + Integer.toHexString(section.letter) + "_" + (page + 1) + ".html'>%s [" + (page + 2) + "]</a>";
                }

                writeNavigationBar(writer, firstPage, prevPage, page, nextPage, lastPage);
                writer.write("<br>");

                writeTableHeader(writer);

                String lastAuthor = "";
                for (int i = page*pageSize; i < Math.min(section.count, (page+1)*pageSize); i++) {
                    Book book = library.getBook(section.firstBookIndex + i);

                    String author = (book.authors == null ? "" : book.authors);
                    if (author.equals(lastAuthor)) author = "";
                    else lastAuthor = author;

                    writeBook(writer, book, author);
                }

                writer.write("</table>\n");
                writer.write("</body>\n");
                writer.write("</html>\n");
                writer.close();
            }
        }
    }

    private void writeTitle(Writer writer, String name) throws IOException {
        writer.write("\n<h3>" + name + "</h3>\n");
    }

    private void writeHtmlHeader(Writer writer, String name) throws IOException {
        writer.write(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">" +
            "<title>LibRuSec " + name + "</title>\n" +
            "<link rel='stylesheet' href='default.css'>\n" +
            "<script type='text/javascript' src='zip.js'></script>\n" +
            "<script type='text/javascript' src='code.js'></script>\n" +
            "</head>\n" +
            "<body>");
    }

    private void writeBook(Writer writer, Book book, String author) throws IOException {
        writer.write(
            "<tr>" +
            "<td>" + author + "</td>" +
            "<td><a href='#' onclick=\"dwl(this,'" + book.zipFileName + "','" + book.fileName + "')\">" + (book.title == null ? "" : book.title) + "</a></td>" +
            "<td>" + (book.genre == null ? "" : book.genre) + "</td>" +
            "<td>" + (book.date == null ? "" : book.date) + "</td>" +
            "<td>" + (book.annotation == null ? "" : book.annotation) + "</td>" +
            "</tr>\n");
    }

    private void writeNavigationBar(Writer writer, String firstPage, String prevPage, int page, String nextPage, String lastPage) throws IOException {
        writer.write(
            "<table>\n" +
                "<tr>" +
                "<td><a href='index.html'>\u041e\u0433\u043b\u0430\u0432\u043b\u0435\u043d\u0438\u0435</a></td>" + // Index
                "<td>" + String.format(firstPage, "\u0412 \u043d\u0430\u0447\u0430\u043b\u043e") + "</td>" + // First
                "<td>" + String.format(prevPage, "&lt;&lt; \u041d\u0430\u0437\u0430\u0434") + "</td>" + // Prev
                "<td>[" + (page + 1) + "]</td>" +
                "<td>" + String.format(nextPage, "\u0412\u043f\u0435\u0440\u0435\u0434 &gt;&gt;") + "</td>" + // Next
                "<td>" + String.format(lastPage, "\u0412 \u043a\u043e\u043d\u0435\u0446") + "</td>" + // Last
                "</tr>\n" +
                "</table>\n");
    }

    private void writeTableHeader(Writer writer) throws IOException {
        writer.write(
            "<table style='width:100%'>\n" +
                "<tr>" +
                "<th>\u0410\u0432\u0442\u043e\u0440</th>" + // Authors
                "<th>\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435</th>" + // Title
                "<th>\u0416\u0430\u043d\u0440</th>" + // Genre
                "<th>\u0414\u0430\u0442\u0430</th>" + // Date
                "<th>\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435</th>" + // Annotations
                "</tr>\n");
    }
}