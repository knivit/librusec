package com.tsoft.librusec;

import org.junit.Test;

import java.io.IOException;

public class MainTest {
    @Test
    public void test() throws IOException {
        String folder = "D:/cygwin64/home/vitaliy_knyazev/books/librusec";
        Main main = new Main();
        main.parse(folder);
    }
}