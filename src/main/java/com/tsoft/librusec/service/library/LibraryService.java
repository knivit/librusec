package com.tsoft.librusec.service.library;

import com.tsoft.librusec.service.cache.CacheFactory;
import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.config.ConfigService;
import com.tsoft.librusec.service.library.group.ByAuthorGroup;
import com.tsoft.librusec.service.library.group.ByYearGroup;
import com.tsoft.librusec.service.parser.Fb2Parser;
import com.tsoft.librusec.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LibraryService {
    private static final int CHUNK_SIZE = 10_000;

    private final ConfigService configService = new ConfigService();

    public void process(Config config, File[] files) {
        log.info("Processing directory {}", config.getBooksFolder());

        int totalCount = 0;
        long totalTime = 0;
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < files.length; i ++) {
            String fileName = files[i].getName();
            System.out.print("File " + (i + 1) + " of " + files.length + " " + fileName + ": ");

            long millis = System.currentTimeMillis();

            Fb2Parser parser = new Fb2Parser();
            books = parser.parse(files[i]);

            if (books.size() >= CHUNK_SIZE) {
                Library library = new Library(books);
                serializeToFile(library, config.getSystemFolder() + "/" + FileUtil.changeExtension(fileName, ".ser"));
                books.clear();
            }

            long time = (System.currentTimeMillis() - millis) / 1000;
            System.out.println(" read in " + time + " sec, " + books.size() + " book(s) found");

            totalCount += books.size();
            totalTime += time;
        }

        if (!books.isEmpty()) {
            Library library = new Library(books);
            serializeToFile(library, config.getSystemFolder() + "/" + FileUtil.changeExtension("todo", ".ser"));
        }

        log.info("{} file(s) processed in {} sec", totalCount, totalTime);
    }

    public ArrayList<ByAuthorGroup> groupByAuthor(Library library) {
        sortByAuthor(library);

        ArrayList<ByAuthorGroup> groups = new ArrayList<>();

        ByAuthorGroup group = new ByAuthorGroup();
        group.letter = 0; // All chars before Russian 'A'
        group.firstBookIndex = 0;
        group.count = 0;
        groups.add(group);

        int n = 0;
        for (Book book : library.getBooks()) {
            char letter = Character.toUpperCase(book.authors.charAt(0));

            if (group.letter != letter) {
                if (letter >= 0x410) {
                    group = new ByAuthorGroup();
                    group.letter = letter;
                    group.firstBookIndex = n;
                    groups.add(group);
                }
            }
            group.count ++;
            n ++;
        }

        return groups;
    }

    public ArrayList<ByYearGroup> groupByYear(Library library) {
        sortByYear(library);

        ArrayList<ByYearGroup> groups = new ArrayList<>();

        ByYearGroup group = new ByYearGroup();
        group.year = "<unknown>";
        group.firstBookIndex = 0;
        group.count = 0;
        groups.add(group);

        int n = 0;
        for (Book book : library.getBooks()) {
            String year = isYear(book.year) ? book.year : "unknown";

            if (!group.year.equals(year)) {
                group = new ByYearGroup();
                group.year = year;
                group.firstBookIndex = n;
                groups.add(group);
            }
            group.count ++;
            n ++;
        }

        return groups;
    }

    private boolean isYear(String text) {
        return (text != null && (text.length() == 4) && Character.isDigit(text.charAt(0)));
    }

    public Library getLibrary() {
        return CacheFactory.getLibraryCache().get("library", () -> load());
    }

    private Library load() {
        Config config = configService.getConfig();
        log.info("Loading library {}", config.getSystemFolder());

        File root = new File(config.getSystemFolder());
        File[] files = root.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            return new Library();
        }

        Library library = new Library();
        for (File file : files) {
            Library fromFile = deserializeFromFile(file.getAbsolutePath());
            unionAll(library, fromFile);
        }

        log.info("Library loaded, {} records", library.getBooks().size());
        return library;
    }

    private void serializeToFile(Library library, String fileName) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(library);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Library deserializeFromFile(String fileName) {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
             ObjectInputStream ois = new ObjectInputStream(in)) {
            return (Library)ois.readObject();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void unionAll(Library dest, Library src) {
        dest.getBooks().addAll(src.getBooks());
    }

    private void sortByAuthor(Library library) {
        library.getBooks().sort((b1, b2) -> b1.authors.compareToIgnoreCase(b2.authors));
    }

    private void sortByYear(Library library) {
        library.getBooks().sort((b1, b2) ->
            (b1.year == null) ?
                ((b2.year == null) ? 0 : -1) :
                    (b2.year == null) ? 1 : b1.year.compareToIgnoreCase(b2.year));
    }
}
