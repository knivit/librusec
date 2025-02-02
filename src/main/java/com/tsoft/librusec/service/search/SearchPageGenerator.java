package com.tsoft.librusec.service.search;

import com.tsoft.librusec.service.library.Book;
import com.tsoft.librusec.service.library.Library;
import com.tsoft.librusec.service.library.LibraryService;

public class SearchPageGenerator {

    private final LibraryService libraryService = new LibraryService();

    public SearchResponse generate(SearchRequest request) {
        Library library = libraryService.getLibrary();
        for (Book book : library.getBooks()) {
            
        }
        return null;
    }
}
