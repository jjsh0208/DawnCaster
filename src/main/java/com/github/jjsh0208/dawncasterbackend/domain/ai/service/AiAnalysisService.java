package com.github.jjsh0208.dawncasterbackend.domain.ai.service;


import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysis;
import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysisImpact;
import com.github.jjsh0208.dawncasterbackend.domain.ai.repository.AiAnalysisRepository;
import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.category.repository.CategoryRepository;
import com.github.jjsh0208.dawncasterbackend.domain.news.entity.News;
import com.github.jjsh0208.dawncasterbackend.domain.news.repository.NewsRepository;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.client.GeminiClient;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.dto.AiAnalysisResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final CategoryRepository categoryRepository;
    private final NewsRepository newsRepository;
    private final GeminiClient geminiClient;
    private final AiAnalysisRepository aiAnalysisRepository;

    // 1. 카테고리 추출 및 각 마테고리별 파이프라인 실행
    public void generateDailyKoreanMarketAnalysis() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            executePipelineForCategory(category, today, startOfDay, endOfDay);
        }
    }

    // 2. 단일 카테고리 단위의 파이프라인 실행 로직
    private void executePipelineForCategory(Category category, LocalDate today, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        // 중복 실행 방지
        if (aiAnalysisRepository.existsByCategoryIdAndAnalysisDate(category.getId(), today)) {
            log.info("[AI Analysis] 오늘 날짜의 한국 증시 분석 결과가 이미 존재합니다. (Category ID: {})", category.getId());
            return;
        }

        try {
            // 분석 뉴스 수집
            List<News> todayNews = fetchTargetNews(category, startOfDay, endOfDay);

            if (todayNews.isEmpty()) {
                log.warn("[AI Analysis] 오늘 수집된 뉴스가 없어 분석을 생략합니다. (Category ID: {})", category.getId());
                return;
            }

            String newsData = formatNewsForAi(todayNews);

            // 외부 API 호출
            log.info("[AI Analysis] 글로벌 뉴스 기반 한국 증시 영향 분석 시작... (Category ID: {})", category.getId());
            AiAnalysisResultDto dto = requestAiSummary(newsData);

            // 결과 저장 메서드 호출
            saveAnalysisResult(category.getId(), today, dto);

            // API Rate Limit 방어를 위한 대기
            Thread.sleep(10000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
            log.error("[AI Analysis] API 호출 대기 중 인터럽트 발생: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[AI Analysis] 분석 파이프라인 실패 (Category ID: {}): {}", category.getId(), e.getMessage(), e);
        }
    }

    // 3. 특정 카테고리 및 시간대 조건에 맞는 뉴스 리스트 조회 로직
    private List<News> fetchTargetNews(Category category, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return newsRepository.findTop15ByCategoryAndPublishedAtBetweenOrderByPublishedAtDesc(
                category, startOfDay, endOfDay);
    }

    private String formatNewsForAi(List<News> newsList) {
        return newsList.stream()
                .map(n -> String.format("- [%s] %s: %s", n.getSource(), n.getTitle(), n.getContent()))
                .collect(Collectors.joining("\n\n"));
    }

    // 4. 추출된 텍스트를 바탕으로 Gemini API 호출 및 DTO 변환 로직
    private AiAnalysisResultDto requestAiSummary(String newsData) {
        return geminiClient.summarizeSectorNews(newsData);
    }

    // 5. DB 저장 전용 메서드
    private void saveAnalysisResult(Long categoryId, LocalDate today, AiAnalysisResultDto dto) {
        AiAnalysis analysis = AiAnalysis.builder()
                .categoryId(categoryId)
                .analysisDate(today)
                .issueTitle(dto.getIssue_title())
                .summary(dto.getSummary())
                .build();

        for (AiAnalysisResultDto.ImpactDto impactDto : dto.getImpacts()) {
            AiAnalysisImpact impact = new AiAnalysisImpact(
                    impactDto.getSector_name(),
                    impactDto.getImpact_type(),
                    impactDto.getReasoning(),
                    impactDto.getRelated_stocks()
            );
            analysis.addImpact(impact);
        }

        aiAnalysisRepository.save(analysis);
        log.info("[AI Analysis] 한국 증시 영향 분석 완료 및 DB 저장 성공 (메일 제목: {})", dto.getEmail_subject());
    }

}
