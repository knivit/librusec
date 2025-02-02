package com.tsoft.librusec.service.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Properties;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Config {

    public static final String VERSION = "1.0";

    private String booksFolder;
    private String cacheFolder;
    private String systemFolder;

    public static Config from(Properties props) {
        return Config.builder()
            .booksFolder(props.getProperty("booksFolder"))
            .cacheFolder(props.getProperty("cacheFolder"))
            .systemFolder(props.getProperty("systemFolder"))
            .build();
    }
}
