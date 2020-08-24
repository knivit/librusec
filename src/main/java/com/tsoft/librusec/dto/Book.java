package com.tsoft.librusec.dto;

import java.io.Serial;
import java.io.Serializable;

public class Book implements Serializable {

    @Serial
    public static final long serialVersionUID = 2L;

    public String zipFileName;
    public String fileName;
    public String authors;
    public String genre;
    public String title;
    public String lang;
    public String annotation;
    public String date;
}
