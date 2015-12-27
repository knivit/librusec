package com.tsoft.librusec.consumer;

import com.tsoft.librusec.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchConsumersManager implements Consumer {
    private List<BatchConsumer> registeredConsumers = new ArrayList<>();
    private List<BatchConsumer> activeConsumers = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();

    public void registerConsumer(BatchConsumer consumer) {
        registeredConsumers.add(consumer);
    }

    @Override
    public void open(String outputFolder) throws IOException {
        for (BatchConsumer consumer : registeredConsumers) {
            consumer.open(outputFolder);
            activeConsumers.add(consumer);
        }
    }

    @Override
    public void accept(Book book) throws IOException {
        books.add(book);
        for (BatchConsumer consumer : activeConsumers) {
            if (consumer.getBatchSize() > 0 && (books.size() % consumer.getBatchSize()) == 0) {
                consumer.acceptBatch(new ArrayList<>(books.subList(books.size() - consumer.getBatchSize(), books.size())));
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (BatchConsumer consumer : activeConsumers) {
            if (consumer.getBatchSize() == -1) {
                consumer.acceptBatch(new ArrayList<>(books));
            }
            consumer.close();
        }
    }
}
