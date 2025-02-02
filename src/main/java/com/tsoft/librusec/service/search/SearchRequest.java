package com.tsoft.librusec.service.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchRequest {

    private String lang;
    private String genre;
    private String title;
    private String annotation;
    private List<String> keywords;
}
