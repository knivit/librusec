package com.tsoft.librusec.service.writer.html;

import com.tsoft.librusec.service.library.Book;
import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.library.Library;
import com.tsoft.librusec.service.library.group.ByAuthorGroup;
import com.tsoft.librusec.service.writer.LibraryWriter;
import com.tsoft.librusec.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class HtmlLibraryWriter implements LibraryWriter {

    private static final int PAGE_SIZE = 500;

    private final IndexPageWriter indexPageWriter = new IndexPageWriter();

    @Override
    public void process(Config config, Library library) throws Exception {
        log.info("Generating HTML index {}", config.getHtmlFolder());

        prepare(config.getHtmlFolder());

        indexPageWriter.write(config.getHtmlFolder(), library);

       /* for (ByAuthorGroup group : byAuthorGroups) {
            for (int page = 0; page * PAGE_SIZE < group.count; page ++) {
                writePage(config.getHtmlFolder(), library, group, page);
            }
        }*/
    }

    private void prepare(String outputFolder) throws Exception {
        for (String fileName : new String[] {"/code.js", "/default.css", "/favicon.ico"}) {
            Path src = Paths.get(getClass().getResource(fileName).toURI());
            Path dst = Paths.get(outputFolder, fileName);
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void writePage(String outputFolder, Library library, ByAuthorGroup group, int page) throws IOException {
        String outputFileName = outputFolder + "/a_" + Integer.toHexString(group.letter) + "_" + page + ".html";

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8))) {
            String name = (group.letter == 0 ? "0-9, eng" : String.valueOf(group.letter));
            writeHtmlHeader(writer, name);
            writeTitle(writer, name);

            String firstPage = "%s", prevPage = "%s";
            if (page > 0) {
                firstPage = "<a href='a_" + Integer.toHexString(group.letter) + "_0.html'>%s [1]</a>";
                prevPage = "<a href='a_" + Integer.toHexString(group.letter) + "_" + (page - 1) + ".html'>%s [" + page + "]</a>";
            }

            String lastPage = "%s", nextPage = "%s";
            int n = (group.count / PAGE_SIZE);
            if (page < n) {
                lastPage = "<a href='a_" + Integer.toHexString(group.letter) + "_" + n + ".html'>%s [" + (n + 1) + "]</a>";
                nextPage = "<a href='a_" + Integer.toHexString(group.letter) + "_" + (page + 1) + ".html'>%s [" + (page + 2) + "]</a>";
            }

            writeNavigationBar(writer, firstPage, prevPage, page, nextPage, lastPage);
            writer.write("<br>");

            writeTableHeader(writer);
            String lastAuthor = "";
            for (int i = page * PAGE_SIZE; i < Math.min(group.count, (page + 1) * PAGE_SIZE); i++) {
                Book book = library.getBook(group.firstBookIndex + i);

                String author = (book.authors == null ? "" : book.authors);
                if (author.equals(lastAuthor)) author = "";
                else lastAuthor = author;

                writeBook(writer, book, author);
            }
            writeTableFooter(writer);

            writeHtmlFooter(writer);
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
            "<script type='text/javascript' src='code.js'></script>\n" +
            "</head>\n" +
            "<body>");
    }

    private void writeHtmlFooter(BufferedWriter writer) throws IOException {
        writer.write("</body>\n");
        writer.write("</html>\n");
    }

    private void writeBook(Writer writer, Book book, String author) throws IOException {
        writer.write(
            "<tr>" +
                "<td>" + author + "</td>" +
                "<td><a href='#' onclick=\"dwl(this,'" + FileUtil.deleteExtension(book.zipFileName) + "','" + book.fileName + "')\">" + (book.title == null ? "" : book.title) + "</a></td>" +
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

    private void writeTableFooter(BufferedWriter writer) throws IOException {
        writer.write("</table>\n");
    }
}
