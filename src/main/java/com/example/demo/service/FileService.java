package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileService {
    String save(String dirPath, MultipartFile file) throws IOException;

    Map<String, Object> readFileAsByte(String filePath) throws IOException;
}
