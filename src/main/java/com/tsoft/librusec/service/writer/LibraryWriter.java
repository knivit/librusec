package com.tsoft.librusec.service.writer;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.library.Library;

public interface LibraryWriter {

    void process(Config config, Library library) throws Exception;
}
