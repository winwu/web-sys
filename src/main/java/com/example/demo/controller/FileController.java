package com.example.demo.controller;

import com.example.demo.service.FileService;
import com.example.demo.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")

public class FileController {
    FileService fileService = new FileServiceImpl();

    @Value("${upload.path}")
    private String uploadPath;

    @RequestMapping(value = "/news/{filename}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNewsImage(
            @PathVariable("filename") String filename) throws IOException {
        String filePath = uploadPath + File.separator + "news" + File.separator + filename;
        try {
            Map<String, Object> result = fileService.readFileAsByte(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf((String) result.get("contentType")));
            headers.setContentLength((Long) result.get("size"));
            return new ResponseEntity(result.get("data"), headers, HttpStatus.OK);

        } catch(IOException e) {
            System.out.println("FileController error");
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
