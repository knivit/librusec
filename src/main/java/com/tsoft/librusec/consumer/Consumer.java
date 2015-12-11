package com.tsoft.librusec.consumer;

import com.tsoft.librusec.BookTitle;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Consumer {
    public void open(String outputFolder) throws IOException;

    public void accept(BookTitle bookTitle) throws IOException;

    public void close() throws IOException;
}
