package com.tsoft.librusec.service.writer;

import com.tsoft.librusec.dto.Config;
import com.tsoft.librusec.dto.Library;

public interface LibraryWriter {

    void process(Config config, Library library) throws Exception;
}
