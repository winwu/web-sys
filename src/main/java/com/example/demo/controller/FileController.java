package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")

public class FileController {
    @Value("${upload.path}")
    private String uploadPath;

    @RequestMapping(value = "/news/{filename}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getNewsImage(
            @PathVariable("filename") String filename) throws IOException {
        String filePath = uploadPath + File.separator + "news" + File.separator + filename;
        Path path = Paths.get(filePath);
        String contentType = Files.probeContentType(path);

        File target = new File(filePath);
        if (!target.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        InputStream inputStream = new FileInputStream(target);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(contentType));
        headers.setContentLength(Files.size(Paths.get(filePath)));

        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }
}
