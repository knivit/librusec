package com.tsoft.librusec.dto;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LibraryTest {

    @Test
    public void test() {
        Library library = new Library();
        Book book0 = createBook("0");
        library.addBook(book0);
        library.addBook(createBook("English"));

        Book book1 = createBook("\u0420\u0443\u0441\u0441\u043a\u0438\u0439");
        library.addBook(book1);

        ArrayList<Section> sections = library.getSections();

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