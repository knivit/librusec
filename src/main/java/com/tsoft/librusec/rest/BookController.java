package com.tsoft.librusec.rest;

import com.tsoft.librusec.service.download.DownloadResult;
import com.tsoft.librusec.service.download.DownloadService;
import com.tsoft.librusec.service.generator.GenerationResult;
import com.tsoft.librusec.service.generator.HtmlContentGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class BookController {

    private final HtmlContentGenerator htmlContentGenerator = new HtmlContentGenerator();
    private final DownloadService downloadService = new DownloadService();

    /**
     *   -------------------------------------------------------------------------------------------
     *   I Language I Genre    I Year                        I Title            I Annotation       I
     *   -------------------------------------------------------------------------------------------
     *   I - <val1> I - <val1> I between [input] and [input] I contains [input] I contains [input] I
     *   I - <val2> I - <val2> I                             I                  I                  I
     *   I - ...    I - ...    I                             I                  I                  I
     *   -------------------------------------------------------------------------------------------
     *   I Authors: A B ...                                                     I      [Find]      I
     *   -------------------------------------------------------------------------------------------
     *   <Author1>
     *   <Author2>
     *   ...
     */
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public void search(HttpServletResponse response) throws IOException {
        GenerationResult result = htmlContentGenerator.generateIndexPage(response.getWriter());
        response.setStatus(result == GenerationResult.SUCCESS ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/book/{zipFileName}/{bookFileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable("zipFileName") String zipFileName, @PathVariable("bookFileName") String bookFileName) {
        DownloadResult result = downloadService.download(zipFileName + ".zip", bookFileName);

        return switch (result.getStatus()) {
            case SUCCESS -> ok(result.getFileName());
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case FAIL -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
