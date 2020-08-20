package com.tsoft.librusec.service.library;

import com.tsoft.librusec.dto.Book;
import com.tsoft.librusec.dto.Library;
import com.tsoft.librusec.dto.Section;
import com.tsoft.librusec.service.parser.Fb2Parser;
import com.tsoft.librusec.util.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class LibraryService {

    public void process(String folder) throws IOException {
        File[] files = findZipFiles(folder);
        if (files == null) {
            System.out.println("Zip files in " + folder + " not found");
            return;
        }

        int totalCount = 0;
        long totalTime = 0;
        for (int i = 0; i < files.length; i ++) {
            String fileName = files[i].getAbsolutePath();
            System.out.print("File " + (i + 1) + " of " + files.length + " " + files[i].getName() + ": ");

            long millis = System.currentTimeMillis();

            Fb2Parser parser = new Fb2Parser();
            Library library = parser.parse(fileName);
            serializeToFile(library, FileUtil.changeExtension(fileName, ".ser"));

            long time = (System.currentTimeMillis() - millis) / 1000;
            System.out.println(" read in " + time + " sec, " + library.getBookCount() + " book(s) found");

            totalCount += library.getBookCount();
            totalTime += time;
        }

        System.out.println(totalCount + " file(s) processed in " + totalTime + " sec");
        System.out.println("See results in " + folder);
    }

    // books must be sorted by authors
    public ArrayList<Section> getSections(Library library) {
        ArrayList<Section> sections = new ArrayList<>();

        Section section = new Section();
        section.letter = 0; // All chars before Russian 'A'
        section.firstBookIndex = 0;
        section.count = 0;
        sections.add(section);

        int n = 0;
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

    public void serializeToFile(Library library, String fileName) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(library);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Library deserializeFromFolder(String folder) {
        File root = new File(folder);
        File[] files = root.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            return new Library();
        }

        Library result = new Library();
        for (File file : files) {
            Library library = deserializeFromFile(file.getAbsolutePath());
            result.unionAll(library);
        }

        result.sortByAuthor();
        return result;
    }

    private Library deserializeFromFile(String fileName) {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
             ObjectInputStream ois = new ObjectInputStream(in)) {
            return (Library)ois.readObject();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private File[] findZipFiles(String folder) {
        File root = new File(folder);
        return root.listFiles((dir, name) -> name.endsWith(".zip") &&
            !Files.exists(Path.of(FileUtil.changeExtension(dir.getAbsolutePath() + "/" + name, ".ser"))));
    }
}
