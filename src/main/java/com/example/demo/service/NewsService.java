package com.example.demo.service;

import com.example.demo.entity.News;

import java.util.List;

public interface NewsService {
    List<News> getNewsList(String search);

    News findNewsById(long id);
}
