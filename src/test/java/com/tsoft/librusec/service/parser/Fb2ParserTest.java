package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.service.library.Book;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Fb2ParserTest {

    private final Fb2Parser fb2Parser = new Fb2Parser();

    @Test
    public void parse() {
        List<Book> books = fb2Parser.parse(new File(getClass().getResource("/1.zip").getFile()));

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Аббасзаде Гусейн", books.get(0).authors);
        assertNull(books.get(0).date);
        assertNull(books.get(0).annotation);
        assertEquals("ru", books.get(0).lang);
        assertEquals("prose_rus_classic", books.get(0).genre);
        assertEquals("Цветы полевые", books.get(0).title);
    }
}
