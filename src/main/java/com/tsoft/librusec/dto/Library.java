package com.tsoft.librusec.dto;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library implements Serializable {

    public static final long serialVersionUID = 2L;

    private ArrayList<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public void unionAll(Library library) {
        books.addAll(library.books);
    }

    public Book getBook(int n) {
        return books.get(n);
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public int getBookCount() {
        return books.size();
    }

    public void sortByAuthor() {
        books.sort((b1, b2) -> b1.authors.compareToIgnoreCase(b2.authors));
    }

}
