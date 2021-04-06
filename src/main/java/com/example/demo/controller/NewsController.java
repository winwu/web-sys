package com.example.demo.controller;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.FileService;
import com.example.demo.service.impl.FileServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    FileService fileService = new FileServiceImpl();

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

        // @TODO: sort 改為日期
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


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(Long.valueOf(id))) {
            News news = repository.findById(id);
            response.put("code", "SUCCESS");
            response.put("data", news);
        } else {
            response.put("code", "FAILURE");
            response.put("message", HttpStatus.NOT_FOUND);
            response.put("data", null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
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
                response.put("code", "FAILURE");
                response.put("message", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        try {

            repository.save(news);
            response.put("code", "SUCCESS");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("code", "FAILURE");

            if (e instanceof ConstraintViolationException) {
                ConstraintViolationException jdbcEx = (ConstraintViolationException) e;
                Set<ConstraintViolation<?>> constraintViolations = jdbcEx.getConstraintViolations();

                Map<String, String> errors = new HashMap<>();

                for (Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator(); iterator.hasNext(); ) {
                    ConstraintViolation<?> next = iterator.next();
                    errors.put(String.valueOf(next.getPropertyPath()), next.getMessage());
                }
                response.put("errors", errors);
            } else {
                response.put("message", e.getMessage());
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity update(
            @PathVariable("id") Long id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "isDeleteImage", required = false) String isDeleteImage,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();
        News news = repository.getOne(id);

        // @TODO: why content and title can't set to null?
        // The error will be: "Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction"
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
                    response.put("code", "FAILURE");
                    response.put("message", e.getMessage());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }

        try {
            repository.save(news);
            response.put("code", "SUCCESS");
        } catch (Exception e) {
            response.put("code", "FAILURE");
            response.put("message", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("ids") Long[] ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            repository.deleteByIdIn(Arrays.asList(ids));
            response.put("code", "SUCCESS");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("code", "FAILURE");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
