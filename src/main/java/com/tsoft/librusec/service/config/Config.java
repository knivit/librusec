package com.tsoft.librusec.service.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Properties;

@Getter
@Builder
@EqualsAndHashCode
public class Config {

    public static final String VERSION = "1.0";

    private String booksFolder;
    private String cacheFolder;
    private String libraryFolder;
    private String htmlFolder;
    private String csvFolder;

    public static Config from(Properties props) {
        return Config.builder()
            .booksFolder(props.getProperty("booksFolder"))
            .cacheFolder(props.getProperty("cacheFolder"))
            .libraryFolder(props.getProperty("libraryFolder"))
            .htmlFolder(props.getProperty("htmlFolder"))
            .csvFolder(props.getProperty("csvFolder"))
            .build();
    }
}
