package com.github.jjsh0208.dawncasterbackend.domain.news.repository;

import com.github.jjsh0208.dawncasterbackend.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByUrl(String url);
}
