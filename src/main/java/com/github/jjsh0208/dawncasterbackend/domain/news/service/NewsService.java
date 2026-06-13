package com.github.jjsh0208.dawncasterbackend.domain.news.service;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.category.repository.CategoryRepository;
import com.github.jjsh0208.dawncasterbackend.domain.news.entity.News;
import com.github.jjsh0208.dawncasterbackend.domain.news.repository.NewsRepository;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.client.FinnhubClient;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.FinnhubNewsResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final CategoryRepository categoryRepository;
    private final FinnhubClient finnhubClient;
    private final NewsRepository newsRepository;

    @Transactional
    public void ingestDailyNews() {
        List<Category> categories = categoryRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        for (Category category : categories) {
            // 1. 콤마로 분리된 심볼 배열 추출 (예: "NVDA,TSM" -> ["NVDA", "TSM"])
            String[] symbols = category.getSearchKeywords().split(",");

            for (String symbol : symbols) {
                List<FinnhubNewsResponse> apiResponses = finnhubClient.getCompanyNews(symbol.trim(), yesterday, today);

                for (FinnhubNewsResponse res : apiResponses) {
                    // 2. 중복 수집 방지 (URL 멱등성 검증)
                    if (newsRepository.existsByUrlAndCategory(res.getUrl(), category)) {
                        continue;
                    }

                    // 3. 엔티티 매핑 및 저장
                    News news = News.builder()
                            .category(category)
                            .title(res.getHeadline())
                            .content(res.getSummary())
                            .url(res.getUrl())
                            .source(res.getSource())
                            .publishedAt(res.getPublishedAt())
                            .build();

                    newsRepository.save(news);
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
