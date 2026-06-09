package com.github.jjsh0208.dawncasterbackend.domain.news.repository;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByUrlAndCategory(String url, Category category);

    // ✅ 변경: 카테고리 + 발행일(구간) 기준 최신순 15개
    List<News> findTop15ByCategoryAndPublishedAtBetweenOrderByPublishedAtDesc(
            Category category,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
