package com.tsoft.librusec.service.writer;

import com.tsoft.librusec.dto.Library;

import java.io.IOException;

public interface LibraryWriter {

    void open(String outputFolder) throws Exception;

    void process(Library library) throws IOException;
}
