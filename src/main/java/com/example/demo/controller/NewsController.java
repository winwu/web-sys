package com.example.demo.controller;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsRepository repository;

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
    public ResponseEntity add(@RequestBody News news) {
        Map<String, Object> response = new HashMap<>();

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
    public ResponseEntity update(@RequestBody News news) {
        // 判斷 id 是存在的，否則會變成 create 一新的
        Map<String, Object> response = new HashMap<>();

        try {
            repository.save(news);
            response.put("code", "SUCCESS");
        } catch (Exception e) {
            response.put("code", "FAILURE");
            response.put("message", e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") long id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);
            response.put("code", "SUCCESS");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("code", "FAILURE");
            response.put("message", "Id not exists");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
