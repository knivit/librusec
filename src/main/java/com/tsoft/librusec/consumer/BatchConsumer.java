package com.tsoft.librusec.consumer;

import com.tsoft.librusec.Book;

import java.io.IOException;
import java.util.ArrayList;

public abstract class BatchConsumer {
    protected String outputFolder;
    private int batchSize;

    protected void open(String outputFolder) throws Exception {
        this.outputFolder = outputFolder;
    }

    protected abstract void acceptBatch(ArrayList<Book> list) throws IOException;

    protected void close() throws IOException { }

    BatchConsumer(int batchSize) {
        assert batchSize != 0;
        this.batchSize = batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
