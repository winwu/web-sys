package com.example.demo.service.impl;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsRepository repository;

    @Override
    public List<News> getNewsList() {
        return repository.findAll();
    }

    @Override
    public News findNewsById(long id) {
        return repository.findById(id);
    }
}
