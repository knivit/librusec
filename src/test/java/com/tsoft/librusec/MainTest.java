package com.tsoft.librusec;

import org.junit.Test;

import java.io.IOException;

public class MainTest {
    @Test
    public void test() throws IOException {
        String folder = Main.class.getResource("").getPath();
        Main main = new Main();
        main.parse(folder);
    }
}