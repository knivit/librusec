package com.tsoft.librusec.consumer;

import com.tsoft.librusec.Book;

import java.io.IOException;

public interface Consumer {
    public void open(String outputFolder) throws Exception;

    public void accept(Book book) throws IOException;

    public void close() throws IOException;
}
