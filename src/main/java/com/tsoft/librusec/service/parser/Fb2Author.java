package com.tsoft.librusec.service.parser;

import com.tsoft.librusec.util.StringUtils;

public class Fb2Author {

    public String firstName;
    public String middleName;
    public String lastName;

    // "last first"
    public String get() {
        String value = lastName;
        if (!StringUtils.isBlank(firstName)) {
            if (!StringUtils.isBlank(value)) {
                value += " " + firstName;
            } else {
                value = firstName;
            }
        }
        return value;
    }
}
