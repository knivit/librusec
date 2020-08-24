package com.tsoft.librusec.service.library;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Library implements Serializable {

    @Serial
    public static final long serialVersionUID = 2L;

    private List<Book> books;

    public Library() {
        books = new ArrayList<>();
    }

    public Library(List<Book> books) {
        this.books = Objects.requireNonNull(books);
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBook(int i) {
        return books.get(i);
    }
}
