package com.github.jjsh0208.dawncasterbackend.domain.news.service;

import com.github.jjsh0208.dawncasterbackend.domain.category.repository.CategoryRepository;
import com.github.jjsh0208.dawncasterbackend.domain.news.repository.NewsRepository;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.client.FinnhubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final CategoryRepository categoryRepository;
    private final FinnhubClient finnhubClient;
    private final NewsRepository newsRepository;


}
