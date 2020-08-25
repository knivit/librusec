package com.tsoft.librusec.service.library;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.parser.Fb2Parser;
import com.tsoft.librusec.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LibraryService {

    public void process(Config config) throws IOException {
        log.info("Processing directory {}", config.getBooksFolder());

        File[] files = findZipFiles(config);
        if (files == null) {
            log.error("Non-processed zip files in {} not found, processing skipped", config.getBooksFolder());
            return;
        }

        int totalCount = 0;
        long totalTime = 0;
        for (int i = 0; i < files.length; i ++) {
            String fileName = files[i].getName();
            System.out.print("File " + (i + 1) + " of " + files.length + " " + fileName + ": ");

            long millis = System.currentTimeMillis();

            Fb2Parser parser = new Fb2Parser();
            List<Book> books = parser.parse(files[i]);

            Library library = new Library(books);
            serializeToFile(library, config.getLibraryFolder() + "/" + FileUtil.changeExtension(fileName, ".ser"));

            long time = (System.currentTimeMillis() - millis) / 1000;
            System.out.println(" read in " + time + " sec, " + books.size() + " book(s) found");

            totalCount += books.size();
            totalTime += time;
        }

        log.info("{} file(s) processed in {} sec", totalCount, totalTime);
    }

    public ArrayList<Section> getSections(Library library) {
        ArrayList<Section> sections = new ArrayList<>();

        Section section = new Section();
        section.letter = 0; // All chars before Russian 'A'
        section.firstBookIndex = 0;
        section.count = 0;
        sections.add(section);

        int n = 0;
        sortByAuthor(library);
        for (Book book : library.getBooks()) {
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

    public Library load(Config config) {
        log.info("Loading library {}", config.getLibraryFolder());

        File root = new File(config.getLibraryFolder());
        File[] files = root.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            return new Library();
        }

        Library result = new Library();
        for (File file : files) {
            Library library = deserializeFromFile(file.getAbsolutePath());
            unionAll(result, library);
        }

        return result;
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

    private File[] findZipFiles(Config config) {
        File root = new File(config.getBooksFolder());
        return root.listFiles((dir, name) -> name.endsWith(".zip") &&
            !Files.exists(Path.of(config.getLibraryFolder(), FileUtil.changeExtension(name, ".ser"))));
    }

    private void unionAll(Library dest, Library src) {
        dest.getBooks().addAll(src.getBooks());
    }

    private void sortByAuthor(Library library) {
        library.getBooks().sort((b1, b2) -> b1.authors.compareToIgnoreCase(b2.authors));
    }
}
