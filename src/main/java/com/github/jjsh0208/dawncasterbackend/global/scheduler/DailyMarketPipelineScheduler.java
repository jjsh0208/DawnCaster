package com.github.jjsh0208.dawncasterbackend.global.scheduler;

import com.github.jjsh0208.dawncasterbackend.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyMarketPipelineScheduler {

    private final NewsService newsService;

    /**
     * 일일 시장 데이터 파이프라인 가동
     * 실행 주기: 매일 오전 5시 정각 (초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 5 * * *")
    public void executeDailyPipeline() {
        try{

            log.info(">> Step 1. Finnhub 뉴스 데이터 수집 시작");
            newsService.ingestDailyNews();
            log.info(">> Step 2. Finnhub 뉴스 데이터 수집 완료");

            // 2단계: AI 요약 분석 (예정)

            // 3단계: 구독자 메일 발송 (예정)
        }
        catch (Exception e) {
        // 파이프라인 중간에 에러가 발생하더라도 애플리케이션이 종료되지 않도록 방어
        log.error("[Pipeline Error] 파이프라인 실행 중 심각한 오류 발생: {}", e.getMessage(), e);
        }
    }
}
