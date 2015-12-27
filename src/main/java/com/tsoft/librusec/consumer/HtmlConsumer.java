package com.tsoft.librusec.consumer;

import com.tsoft.librusec.Book;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HtmlConsumer extends BatchConsumer {
    public HtmlConsumer() {
        super(-1);
    }

    public static void sortByAuthor(ArrayList<Book> books) {
        books.sort((b1, b2) -> b1.authors.compareToIgnoreCase(b2.authors));
    }

    // books must be sorted by authors
    public static ArrayList<Section> fillSections(ArrayList<Book> sortedBooks) {
        ArrayList<Section> sections = new ArrayList<>();

        Section section = new Section();
        section.letter = 0; // All chars before Russian 'A'
        section.firstBookIndex = 0;
        section.count = 0;
        sections.add(section);

        int n = 0;
        for (Book book : sortedBooks) {
            char letter = Character.toUpperCase(book.authors.charAt(0));
            if (section.letter != letter) {
                if (letter >= 0x410) {
                    section = new Section();
                    section.letter = letter;
                    section.firstBookIndex = n;
                    sections.add(section);
                }
            }
            section.count ++;
            n ++;
        }
        return sections;
    }

    @Override
    public void acceptBatch(ArrayList<Book> books) throws IOException {
        sortByAuthor(books);
        ArrayList<Section> sections = fillSections(books);

        String outputFileName = outputFolder + "/index.html";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));
        writer.write(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">" +
            "<title>LibRuSec</title>\n" +
            "<style>\n" +
            "table, th, td {\n" +
            "border: 1px solid black;\n" +
            "border-collapse: collapse;\n" +
            "}\n" +
            "th, td {\n" +
            "padding: 8px;\n" +
            "}\n" +
            "tr:nth-child(even) {background: #FEFEFE}\n" +
            "tr:nth-child(odd) {background: #FFFFFF}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>");

        writer.write("\n<h2>By authors:</h2>\n");
        for (Section section : sections) {
            String name = (section.letter == 0 ? "0-9, eng" : String.valueOf(section.letter));
            writer.write("<a href='a_" + Integer.toHexString(section.letter) + "_0.html'>" + name + " (" + section.count + ")</a>");
            writer.write("<br>");
        }
        writer.write("\n</body>\n</html>\n");
        writer.close();

        int pageSize = 500;
        for (Section section : sections) {
            for (int page = 0; page * pageSize < section.count; page++) {
                outputFileName = outputFolder + "/a_" + Integer.toHexString(section.letter) + "_" + page + ".html";
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));

                String name = (section.letter == 0 ? "0-9, eng" : String.valueOf(section.letter));
                writer.write(
                        "<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "<head>\n" +
                                "<meta charset=\"utf-8\">" +
                                "<title>LibRuSec " + name + "</title>\n" +
                                "<style>\n" +
                                "table, th, td {\n" +
                                "border: 1px solid black;\n" +
                                "border-collapse: collapse;\n" +
                                "}\n" +
                                "th, td {\n" +
                                "padding: 4px;\n" +
                                "}\n" +
                                "tr:nth-child(even) {background: #FEFEFE}\n" +
                                "tr:nth-child(odd) {background: #FFFFFF}\n" +
                                "</style>\n" +
                                "</head>\n" +
                                "<body>");

                writer.write("\n<h3>" + name + "</h3>\n");
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

                writer.write("<table style='width:100%'>\n" +
                        "<tr>" +
                        "<td><a href='index.html'>Index</a></td>" +
                        "<td>" + String.format(firstPage, "First") + "</td>" +
                        "<td>" + String.format(prevPage, "&lt;&lt; Prev") + "</td>" +
                        "<td>[" + (page + 1) + "]</td>" +
                        "<td>" + String.format(nextPage, "Next &gt;&gt;") + "</td>" +
                        "<td>" + String.format(lastPage, "Last") + "</td>" +
                        "</tr>\n" +
                        "</table>\n");

                writer.write("<table style='width:100%'>\n" +
                        "<tr><th>Lang</th><th>Genre</th><th>Date</th><th>Authors</th><th>Title</th><th>Annotations</th><th>Link</th></tr>\n");

                String lastAuthor = "";
                for (int i = page*pageSize; i < Math.min(section.count, (page+1)*pageSize); i++) {
                    Book book = books.get(section.firstBookIndex + i);

                    String author = (book.authors == null ? "" : book.authors);
                    if (author.equals(lastAuthor)) author = "";
                    else lastAuthor = author;

                    writer.write(
                            "<tr>" +
                                    "<td>" + (book.lang == null ? "" : book.lang) + "</td>" +
                                    "<td>" + (book.genre == null ? "" : book.genre) + "</td>" +
                                    "<td>" + (book.date == null ? "" : book.date) + "</td>" +
                                    "<td>" + author + "</td>" +
                                    "<td>" + (book.title == null ? "" : book.title) + "</td>" +
                                    "<td>" + (book.annotation == null ? "" : book.annotation) + "</td>" +
                                    "<td>" + book.zipFileName + "#" + book.fileName + "</td>" +
                                    "</tr>\n");
                }
                writer.write("</table>\n");
                writer.write("</body>\n");
                writer.write("</html>\n");
                writer.close();
            }
        }
    }
}
