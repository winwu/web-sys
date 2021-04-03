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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (repository.existsById(Long.valueOf(id))) {
            News news = repository.findById(id);
            return ResponseEntity.ok().body(news);
        } else {
            // 404
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String add(@RequestBody News news) {
        repository.save(news);
        return "success";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public String update(@RequestBody News news) {
        // @TODO
        // 判斷 id 是存在的，否則會變成 create 一新的
        repository.save(news);
        return "success";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "success";
        } else {
            return "id not found";
        }
    }
}
