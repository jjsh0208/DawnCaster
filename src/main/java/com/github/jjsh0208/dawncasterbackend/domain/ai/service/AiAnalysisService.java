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
import org.springframework.transaction.annotation.Transactional;

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

    // 1. 전체 실행 파이프라인 (트랜잭션 미적용: 외부 API 호출 및 대기 시 DB 커넥션 점유 방지)
    public void generateDailyKoreanMarketAnalysis() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            // 중복 실행 방지 (return -> continue로 수정)
            if (aiAnalysisRepository.existsByCategoryIdAndAnalysisDate(category.getId(), today)) {
                log.info("[AI Analysis] 오늘 날짜의 한국 증시 분석 결과가 이미 존재합니다. (Category ID: {})", category.getId());
                continue;
            }

            // 분석 뉴스 수집
            List<News> todayNews = newsRepository.findTop15ByCategoryAndPublishedAtBetweenOrderByPublishedAtDesc(
                    category, startOfDay, endOfDay);

            if (todayNews.isEmpty()) {
                log.warn("[AI Analysis] 오늘 수집된 뉴스가 없어 분석을 생략합니다. (Category ID: {})", category.getId());
                continue;
            }

            String newsData = formatNewsForAi(todayNews);

            try {
                // 외부 API 호출 (DB 커넥션을 점유하지 않은 상태)
                log.info("[AI Analysis] 글로벌 뉴스 기반 한국 증시 영향 분석 시작... (Category ID: {})", category.getId());
                AiAnalysisResultDto dto = geminiClient.summarizeSectorNews(newsData);

                // 결과 저장 메서드 호출
                saveAnalysisResult(category.getId(), today, dto);

                Thread.sleep(10000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 상태 복구
                log.error("[AI Analysis] API 호출 대기 중 인터럽트 발생: {}", e.getMessage());
            } catch (Exception e) {
                log.error("[AI Analysis] 분석 파이프라인 실패 (Category ID: {}): {}", category.getId(), e.getMessage(), e);
            }
        }
    }

    // 2. DB 저장 전용 메서드 (Spring Data JPA의 기본 트랜잭션 활용)
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


    @Transactional(readOnly = true)
    public AiAnalysis getTodayAnalysisByCategoryId(Long categoryId) {
        LocalDate today = LocalDate.now();
        return aiAnalysisRepository.findByCategoryIdAndAnalysisDateWithImpacts(categoryId, today)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리의 오늘 분석 데이터가 존재하지 않습니다. 카테고리 ID: " + categoryId));
    }

    private String formatNewsForAi(List<News> newsList) {
        return newsList.stream()
                .map(n -> String.format("- [%s] %s: %s", n.getSource(), n.getTitle(), n.getContent()))
                .collect(Collectors.joining("\n\n"));
    }

}
