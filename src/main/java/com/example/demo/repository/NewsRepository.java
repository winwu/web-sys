package com.example.demo.repository;

import com.example.demo.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    News findById(long id);

    // ref: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

    // Page<News> findByTitleContaining(String title, Pageable pageable);
    Page<News> findByTitleOrContentContaining(String title, String content, Pageable pageable);


    @Modifying
    @Transactional
    void deleteByIdIn(List<Long> ids);

}
