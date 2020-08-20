package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.dto.Library;
import com.tsoft.librusec.service.writer.LibraryWriter;
import com.tsoft.librusec.dto.Book;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Fb2Parser {
    public Fb2Parser() { }

    public Library parse(String fileName) throws IOException {
        Library library = new Library();
        List<String> errors = new ArrayList<>();

        // file names in the zip are UTF-8
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileName), StandardCharsets.UTF_8)) {
            ZipEntry entry;

            int progressBar = 0;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.getName().endsWith(".fb2")) {
                    errors.add("Unsupported extension of " + entry.getName() + ": book skipped");
                    continue;
                }

                Book book = new Book();
                book.zipFileName = new File(fileName).getName();
                book.fileName = entry.getName();

                byte[] buf = new byte[2048];
                int bufSize = zis.read(buf);

                String defaultCharsetName = "windows-1251";
                String text = new String(buf, 0, bufSize, Charset.forName(defaultCharsetName));

                // read the charset
                String charsetName = getValue(text, " encoding=\"", "\"");
                if (charsetName == null) {
                    charsetName = defaultCharsetName;
                    errors.add("Unknown encoding for " + entry.getName() + ": using " + defaultCharsetName);
                }

                if (!defaultCharsetName.equalsIgnoreCase(charsetName)) {
                    text = new String(buf, 0, bufSize, Charset.forName(charsetName));
                }

                String title = getValue(text, "<title-info>", "</title-info>");

                // try to read further (buf size = 2048 doesn't mean all 2048 bytes will be read at once)
                while (title == null) {
                    bufSize = zis.read(buf);
                    if (bufSize == -1) break;

                    text = text + new String(buf, 0, bufSize, Charset.forName(charsetName));
                    title = getValue(text, "<title-info>", "</title-info>");
                }
                if (title == null) {
                    errors.add("Undefined <title-info> for " + entry.getName() + ": book skipped");
                    continue;
                }

                book.genre = getValue(title, "<genre>", "</genre>");
                book.title = getValue(title, "<book-title>", "</book-title>");
                book.lang = getValue(title, "<lang>", "</lang>");
                book.authors = getAuthors(title);
                book.annotation = getValue(title, "<annotation>", "</annotation");
                book.date = getValue(title, "<date>", "</date>");
                library.addBook(book);

                if ((progressBar ++ % 100) == 0) System.out.print('.');
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("\n" + String.join("\n", errors));
        }

        return library;
    }

    private String getAuthors(String title) {
        ArrayList<String> authors = getValueList(title, "<author>", "</author>");
        StringBuilder buf = new StringBuilder();
        for (String author : authors) {
            String firstName = getValue(author, "<first-name>", "</first-name>");
            String middleName = getValue(author, "<middle-name>", "</middle-name>");
            String lastName = getValue(author, "<last-name>", "</last-name>");

            if (buf.length() > 0) buf.append(',');
            if (lastName != null && !lastName.isEmpty()) buf.append(lastName);
            if (middleName != null && !middleName.isEmpty()) buf.append(' ').append(middleName);
            if (firstName != null && !firstName.isEmpty()) buf.append(' ').append(firstName);
        }
        return buf.length() == 0 ? "?" : buf.toString();
    }

    private String getValue(String text, String startTag, String endTag) {
        ArrayList<String> values = getValueList(text, startTag, endTag);
        if (values == null || values.isEmpty()) return null;

        String val = values.get(0);
        if (values.size() > 1) {
            StringBuilder buf = new StringBuilder();
            for (String str : values) {
                if (buf.length() > 0) buf.append(", ");
                buf.append(str);
            }
            val = buf.toString();
        }

        return val;
    }

    private ArrayList<String> getValueList(String text, String startTag, String endTag) {
        int off = 0;
        ArrayList<String> result = new ArrayList<>();
        while (true) {
            int from = text.indexOf(startTag, off);
            if (from == -1) break;

            int to = text.indexOf(endTag, from + startTag.length());
            if (to == -1) break;

            result.add(text.substring(from + startTag.length(), to));
            off = to;
        }

        return result;
    }
}
