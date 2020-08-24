package com.tsoft.librusec.service;

import com.tsoft.librusec.service.library.Book;
import com.tsoft.librusec.service.library.Library;
import com.tsoft.librusec.service.library.Section;
import com.tsoft.librusec.service.library.LibraryService;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LibraryServiceTest {

    private final LibraryService libraryService = new LibraryService();

    @Test
    public void test() {
        Library library = new Library();
        library.getBooks().add(createBook("0"));
        library.getBooks().add(createBook("English"));
        library.getBooks().add(createBook("\u0420\u0443\u0441\u0441\u043a\u0438\u0439"));

        ArrayList<Section> sections = libraryService.getSections(library);

        assertEquals(2, sections.size());
        assertEquals(0, sections.get(0).firstBookIndex);
        assertEquals(2, sections.get(0).count);
        assertEquals(2, sections.get(1).firstBookIndex);
        assertEquals(1, sections.get(1).count);
    }

    private Book createBook(String authors) {
        Book book = new Book();
        book.authors = authors;
        return book;
    }
}
