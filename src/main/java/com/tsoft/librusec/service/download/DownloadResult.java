package com.tsoft.librusec.service.download;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

@Getter
@Builder
@ToString
public class DownloadResult {

    public enum Status {
        SUCCESS,
        NOT_FOUND,
        FAIL
    }

    private Path fileName;
    private Status status;
    private String errorMessage;
}
