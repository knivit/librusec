package com.tsoft.librusec.rest;

import com.tsoft.librusec.service.download.DownloadResult;
import com.tsoft.librusec.service.download.DownloadService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController("/books")
public class BookController {

    private final DownloadService downloadService = new DownloadService();

    @GetMapping("/zipFileName/bookFileName")
    public ResponseEntity<Resource> download(@PathParam("zipFileName") String zipFileName, @PathParam("bookFileName") String bookFileName) {
        DownloadResult result = downloadService.download(zipFileName, bookFileName);

        return switch (result.getStatus()) {
            case SUCCESS -> ok(result.getFileName());
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case FAIL -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }

    private ResponseEntity<Resource> ok(Path file) {
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.toFile().length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }
}
