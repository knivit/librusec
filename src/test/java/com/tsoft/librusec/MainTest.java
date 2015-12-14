package com.tsoft.librusec;

import com.tsoft.librusec.consumer.HtmlConsumer;
import com.tsoft.librusec.consumer.Section;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MainTest {
    @Test
    public void test() throws IOException {
        ArrayList<Book> books = new ArrayList<>();
        Book book0 = createBook("0");
        books.add(book0);
        books.add(createBook("English"));

        Book book1 = createBook("\u0420\u0443\u0441\u0441\u043a\u0438\u0439");
        books.add(book1);

        HtmlConsumer.sortByAuthor(books);
        ArrayList<Section> sections = HtmlConsumer.fillSections(books);

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