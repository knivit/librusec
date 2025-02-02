package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.service.library.Book;
import com.tsoft.librusec.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class Fb2Parser {

    public List<Book> parse(File file) {
        List<Book> books = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            if (file.getName().endsWith(".zip")) {
                // file names in the zip are UTF-8
                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file), StandardCharsets.UTF_8)) {
                    ZipEntry entry;

                    while ((entry = zis.getNextEntry()) != null) {
                        if (!entry.getName().endsWith(".fb2")) {
                            errors.add("Unsupported file extension, skipped: " + entry.getName());
                            continue;
                        }

                        Book book = readBook(file.getName() + "#" + entry.getName(), zis);
                        if (book != null) {
                            book.zipFileName = file.getName();
                            book.fileName = entry.getName();
                            books.add(book);
                        }
                    }
                }
            } else {
                try (InputStream is = new FileInputStream(file)) {
                    Book book = readBook(file.getName(), is);
                    if (book != null) {
                        book.fileName = file.getName();
                        books.add(book);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error processing file {}", file.getAbsolutePath(), ex);
            throw new IllegalStateException(ex);
        }

        if (!errors.isEmpty()) {
            System.err.println("\n" + String.join("\n", errors));
        }

        return books;
    }

    private Book readBook(String fileName, InputStream is) {
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);

            Fb2Book fb2Book = null;
            Fb2Author fb2Author = null;
            Fb2Annotation fb2Annotation = null;

            boolean done = false;
            while (!done && reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "title-info":
                            fb2Book = new Fb2Book();
                            break;
                        case "genre":
                            fb2Book.addGenre(readValue(reader));
                            break;
                        case "author":
                            fb2Author = new Fb2Author();
                            break;
                        case "first-name":
                            fb2Author.firstName = readValue(reader);
                            break;
                        case "middle-name":
                            fb2Author.middleName = readValue(reader);
                            break;
                        case "last-name":
                            fb2Author.lastName = readValue(reader);
                            break;
                        case "book-title":
                            fb2Book.title = readValue(reader);
                            break;
                        case "annotation":
                            fb2Annotation = new Fb2Annotation();
                            fb2Annotation.add(readValue(reader));
                            break;
                        case "p":
                            fb2Annotation.add(readValue(reader));
                            break;
                        case "keywords":
                            fb2Book.keywords = readValue(reader);
                            break;
                        case "date":
                            fb2Book.year = readValue(reader);
                            break;
                        case "lang":
                            fb2Book.lang = readValue(reader);
                            break;
                        case "src-lang":
                            fb2Book.srcLang = readValue(reader);
                            break;
                    }
                }

                if (!nextEvent.isEndElement()) {
                    continue;
                }

                EndElement endElement = nextEvent.asEndElement();
                switch (endElement.getName().getLocalPart()) {
                    case "title-info":
                        done = true;
                        break;
                    case "author":
                        fb2Book.addAuthor(fb2Author);
                        break;
                    case "annotation":
                        fb2Book.annotation = fb2Annotation.value;
                        break;
                }
            }

            if (fb2Book == null) {
                return null;
            }

            Book book = new Book();
            book.title = fb2Book.title;
            book.authors = fb2Book.authors;
            book.genre = fb2Book.genre;
            book.year = fb2Book.year;
            book.lang = fb2Book.lang;
            book.annotation = fb2Book.annotation;

            return book;
        } catch (Exception ex) {
            log.warn("Error parsing file {}", fileName, ex);
            return null;
        }
    }

    private String readValue(XMLEventReader reader) throws XMLStreamException {
        XMLEvent nextEvent = reader.nextEvent();
        return StringUtils.trim(nextEvent.asCharacters().getData());
    }
}
