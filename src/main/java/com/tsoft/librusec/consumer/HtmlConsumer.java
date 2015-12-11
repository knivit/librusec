package com.tsoft.librusec.consumer;

import com.tsoft.librusec.BookTitle;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HtmlConsumer extends BatchConsumer {
    private BufferedWriter writer;

    public HtmlConsumer() {
        super(-1);
    }

    @Override
    public void open(String outputFolder) throws IOException {
        String outputFileName = outputFolder + "/index.html";
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8));
    }

    class Section {
        public char letter;
        public int count;
    }

    ArrayList<Section> byAuthors = new ArrayList<>();

    private void fillByAutors(ArrayList<BookTitle> books) {
        char letter = 0x0;
        Section section = null;
        for (BookTitle book : books) {
            char newLetter = book.authors.charAt(0);
            if (letter != newLetter) {
                if (section != null) byAuthors.add(section);
                section = new Section();
                section.letter = newLetter;
                letter = newLetter;
            }
            section.count ++;
        }
        if (section != null) byAuthors.add(section);
    }

    @Override
    public void acceptBatch(ArrayList<BookTitle> books) throws IOException {
        if (writer == null) return;

        books.sort((b1, b2) -> b1.authors.compareTo(b2.authors));

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

        fillByAutors(books);
        writer.write("\n<h2>By authors:</h2>\n");
        for (Section section : byAuthors) {
            writer.write("<h3><a href='#ba_" + Integer.toString(section.letter) + "'>" + section.letter + " (" + section.count + ")</a></h3>");
            writer.write(" ");
        }

        writer.write("\n<br>\n");

        int n = 0;
        for (Section section : byAuthors) {
            writer.write("\n<h3><a name='#ba_" + Integer.toString(section.letter) + "'>" + section.letter + "</a></h3>\n");
            writer.write("<table style='width:100%'>\n" +
                "<tr><th>Lang</th><th>Genre</th><th>Date</th><th>Authors</th><th>Title</th><th>Annotations</th><th>Link</th></tr>\n");

            String lastAuthor = "";
            while (n < books.size()) {
                BookTitle book = books.get(n);
                if (book.authors.charAt(0) != section.letter) break;

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
                n ++;
            }
            writer.write("</table>\n");
            writer.write("\n<br>\n");
        }

        writer.write("\n</body>\n</html>\n");
    }

    @Override
    public void close() throws IOException {
        if (writer != null) writer.close();
    }
}
