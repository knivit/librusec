package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.util.StringUtils;

public class Fb2Annotation {

    public String value;

    public void add(String str) {
        if (!StringUtils.isBlank(value)) {
            value += " " + str;
        } else {
            value = str;
        }
    }
}
