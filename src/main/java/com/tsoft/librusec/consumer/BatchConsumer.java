package com.tsoft.librusec.consumer;

import com.tsoft.librusec.BookTitle;

import java.io.IOException;
import java.util.ArrayList;

public abstract class BatchConsumer {
    private int batchSize;

    protected abstract void open(String outputFolder) throws IOException;

    protected abstract void acceptBatch(ArrayList<BookTitle> list) throws IOException;

    protected abstract void close() throws IOException;

    BatchConsumer(int batchSize) {
        assert batchSize != 0;
        this.batchSize = batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
