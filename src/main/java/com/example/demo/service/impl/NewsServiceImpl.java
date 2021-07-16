package com.example.demo.service.impl;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "news")
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsRepository repository;

    @Override
    public List<News> getNewsList(String search) {
        // @TODO
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#p0", unless = "#result == null")
    public News findNewsById(long id) {
        return repository.findById(id);
    }
}
