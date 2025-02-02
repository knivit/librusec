package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.util.StringUtils;

public class Fb2Book {

    public String title;
    public String genre;
    public String authors;
    public String annotation;
    public String keywords;
    public String year;
    public String lang;
    public String srcLang;

    public void addGenre(String val) {
        if (!StringUtils.isBlank(genre)) {
            genre += "," + val;
        } else {
            genre = val;
        }
    }

    public void addAuthor(Fb2Author author) {
        if (!StringUtils.isBlank(authors)) {
            authors += "," + author.get();
        } else {
            authors = author.get();
        }
    }
}
