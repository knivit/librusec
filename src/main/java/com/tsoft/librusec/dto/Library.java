package com.tsoft.librusec.dto;

import java.util.ArrayList;

public class Library {

    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Section> sections = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public Book getBook(int n) {
        return books.get(n);
    }

    private void sortByAuthor() {
        books.sort((b1, b2) -> b1.authors.compareToIgnoreCase(b2.authors));
    }

    // books must be sorted by authors
    public ArrayList<Section> getSections() {
        sortByAuthor();

        Section section = new Section();
        section.letter = 0; // All chars before Russian 'A'
        section.firstBookIndex = 0;
        section.count = 0;
        sections.add(section);

        int n = 0;
        for (Book book : books) {
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

}
