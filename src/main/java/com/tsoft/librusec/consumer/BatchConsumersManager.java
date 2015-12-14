package com.tsoft.librusec.consumer;

import com.tsoft.librusec.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchConsumersManager implements Consumer {
    private List<BatchConsumer> consumers = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();

    public void registerConsumer(BatchConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public void open(String outputFolder) throws IOException {
        for (BatchConsumer consumer : consumers) consumer.open(outputFolder);
    }

    @Override
    public void accept(Book book) throws IOException {
        books.add(book);
        for (BatchConsumer consumer : consumers) {
            if (consumer.getBatchSize() > 0 && (books.size() % consumer.getBatchSize()) == 0) {
                consumer.acceptBatch(new ArrayList<>(books.subList(books.size() - consumer.getBatchSize(), books.size())));
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (BatchConsumer consumer : consumers) {
            if (consumer.getBatchSize() == -1) {
                consumer.acceptBatch(new ArrayList<>(books));
            }
            consumer.close();
        }
    }
}
