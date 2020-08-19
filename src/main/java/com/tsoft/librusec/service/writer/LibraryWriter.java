package com.tsoft.librusec.service.writer;

import com.tsoft.librusec.dto.Book;

import java.io.IOException;

public interface LibraryWriter {
    void open(String outputFolder) throws Exception;

    void accept(Book book) throws IOException;

    void close() throws IOException;
}
