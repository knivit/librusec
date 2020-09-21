package com.tsoft.librusec.service.generator;

import com.tsoft.librusec.service.library.Library;
import com.tsoft.librusec.service.library.LibraryService;
import lombok.extern.slf4j.Slf4j;

import java.io.Writer;

@Slf4j
public class HtmlContentGenerator {

    private final LibraryService libraryService = new LibraryService();
    private final IndexPageGenerator indexPageGenerator = new IndexPageGenerator();

    public GenerationResult generateIndexPage(Writer writer) {
        try {
            Library library = libraryService.getLibrary();
            indexPageGenerator.generate(writer, library);
            return GenerationResult.SUCCESS;
        } catch (Exception ex) {
            log.error("Error generating index page", ex);
            try {
                writer.write(ex.getMessage());
            } catch (Exception e) { }
            return GenerationResult.ERROR;
        }
    }
}
