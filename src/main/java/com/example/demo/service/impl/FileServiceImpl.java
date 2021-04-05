package com.example.demo.service.impl;

import com.example.demo.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileServiceImpl implements FileService {
    @Override
    public String save(String dirPath, MultipartFile file) throws IOException {
        if (file == null || file.getSize() == 0) {
            throw new IOException("[FileServiceImpl] file is not exists");
        }

        if (!new File(dirPath).exists()) {
            new File(dirPath).mkdir();
        }

        System.out.println("Real Path to Uploads = " + dirPath);

        Long currentTime = System.currentTimeMillis();
        String newName = currentTime.toString() + "_" + file.getOriginalFilename();

        byte[] bytes = file.getBytes();
        String filePath = dirPath + newName;
        Path path = Paths.get(filePath);
        Files.write(path, bytes);

        return newName;
    }

    @Override
    public Map<String, Object> readFileAsByte(String filePath) throws IOException {

        Map<String, Object> result = new HashMap<>();

        Path path = Paths.get(filePath);
        File target = new File(filePath);

        if (!target.exists()) {
            throw new IOException("[FileServiceImpl] readFileAsByte: " + filePath + " is not exists");
        }
        String contentType = Files.probeContentType(path);
        byte[] data = Files.readAllBytes(path);

        result.put("contentType", contentType);
        result.put("size", Files.size(Paths.get(filePath)));
        result.put("data", data);

        return result;
    }
}
