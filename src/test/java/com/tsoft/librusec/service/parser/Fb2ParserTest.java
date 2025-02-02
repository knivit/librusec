package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.service.library.Book;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Fb2ParserTest {

    private final Fb2Parser fb2Parser = new Fb2Parser();

    @Test
    public void parse_zip() {
        List<Book> books = fb2Parser.parse(new File(getClass().getResource("/1.zip").getFile()));

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Аббасзаде Гусейн", books.get(0).authors);
        assertNull(books.get(0).year);
        assertNull(books.get(0).annotation);
        assertEquals("ru", books.get(0).lang);
        assertEquals("prose_rus_classic", books.get(0).genre);
        assertEquals("Цветы полевые", books.get(0).title);
    }

    @Test
    public void parse_single() {
        List<Book> books = fb2Parser.parse(new File(getClass().getResource("/2.fb2").getFile()));

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Беллок Лаундз Мари Аделаид", books.get(0).authors);
        assertEquals("1911", books.get(0).year);
        assertEquals(
        "Миссис Бантинг вместе с мужем открывает доходный дом, и поначалу дела идут из рук вон плохо. Но через какое-то время у Бантингов появляется новый арендатор: мистер Слут, вежливый и воспитанный молодой человек, готовый щедро платить за комнаты. Правда, у него есть свои странности: он до фанатизма религиозен, постоянно куда-то исчезает по ночам, а потом проводит некие таинственные эксперименты. " +
        "Меж тем по всему Лондону разносятся слухи о серийном убийце. Жестокий маньяк убивает молодых женщин и оставляет на телах жертв крошечные клочки бумаги с надписью «Мститель». Полицейские патрулируют улицы днем и ночью, но никак не могут выйти на его след. " +
        "Постепенно миссис Бантинг начинает подозревать, что их жилец может быть не тем, за кого себя выдает…", books.get(0).annotation);
        assertEquals("ru", books.get(0).lang);
        assertEquals("detective,thriller", books.get(0).genre);
        assertEquals("Жилец", books.get(0).title);
    }
}
