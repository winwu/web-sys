package com.example.demo.service.impl;

import com.example.demo.service.FileService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileServiceImpl implements FileService {
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
}
