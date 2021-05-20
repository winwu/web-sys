package com.example.demo.controller;

import com.example.demo.entity.News;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.FileService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    FileService fileService;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private NewsRepository repository;

    @ApiOperation(value = "Retrieve all news list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "start from which index", defaultValue = "0"),
            @ApiImplicitParam(name = "limit", value = "how many records after start", defaultValue = "10"),
            @ApiImplicitParam(name = "search", value = "search text from title or content field")
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> newsList(
            @RequestParam(required = false) String search,
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        try {
            List<News> news = new ArrayList<>();
            Pageable pageable = PageRequest.of(start, limit, sort);
            Page<News> pageResult;

            if (search == null) {
                pageResult = repository.findAll(pageable);
            } else {
                pageResult = repository.findByTitleOrContentContaining(search, search, pageable);
            }

            news = pageResult.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("data", news);
            response.put("currentPage", pageResult.getNumber());
            response.put("totalRecords", pageResult.getTotalElements());
            response.put("totalPages", pageResult.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    public Page<News> newsList(
    //          @RequestParam(value = "start", defaultValue = "0") Integer start,
    //          @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
    //        start = start < 0 ? 0 : start;
    //        Sort sort = Sort.by(Sort.Direction.DESC, "id");
    //        Pageable pageable = PageRequest.of(start, limit, sort);
    //        Page<News> page = repository.findAll(pageable);
    //
    //        return page;
    //    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 404, message = "Entity not found")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        News news = repository
                .findById(Long.valueOf(id))
                .orElseThrow(() -> new CustomException("Not found", HttpStatus.NOT_FOUND));
        response.put("code", "SUCCESS");
        response.put("data", news);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity add(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Map<String, Object> response = new HashMap<>();

        News news = new News();
        news.setTitle(title);
        news.setContent(content);

        if (image != null && image.getSize() > 0) {
            try {
                String newFileName = fileService.save(uploadPath + File.separator + "news" + File.separator, image);
                news.setImage(newFileName);
            } catch (IOException e) {
                throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        repository.save(news);
        response.put("code", "SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity update(
            @PathVariable("id") Long id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "isDeleteImage", required = false) String isDeleteImage,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();
        Optional<News> newsOptional = repository.findById(id);

        if (newsOptional.isEmpty()) {
            throw new CustomException("Entity is not exists", HttpStatus.BAD_REQUEST);
        }

        try {
            News news = newsOptional.get();
            news.setTitle(title);
            news.setContent(content);

            if (isDeleteImage != null && isDeleteImage.equals("1")) {
                news.setImage(null);
            } else {
                if (image != null && image.getSize() > 0) {
                    try {
                        String newFileName = fileService.save(uploadPath + File.separator + "news" + File.separator, image);
                        news.setImage(newFileName);
                    } catch (IOException e) {
                        throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                }
            }
            repository.save(news);
            response.put("code", "SUCCESS");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Delete success"),
            @ApiResponse(code = 400, message = "Entity is not exists"),
            @ApiResponse(code = 403, message = "Permission denied")
    })
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity delete(@PathVariable("ids") Long[] ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            repository.deleteByIdIn(Arrays.asList(ids));
            response.put("code", "SUCCESS");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
