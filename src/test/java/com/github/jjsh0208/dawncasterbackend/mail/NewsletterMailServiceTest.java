package com.github.jjsh0208.dawncasterbackend.mail;

import com.github.jjsh0208.dawncasterbackend.domain.ai.service.AiAnalysisCacheService;
import com.github.jjsh0208.dawncasterbackend.domain.email.service.NewsletterBatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NewsletterMailServiceTest {

    @Autowired
    private NewsletterBatchService newsletterBatchService;

    @Autowired
    private AiAnalysisCacheService aiAnalysisCacheService;

    @Test
    @DisplayName("수동 메일 발송 파이프라인 통합 테스트")
    void testSendDailyNewsletterPipeline() throws InterruptedException {

        System.out.println("=== 1. Redis 캐시 워밍 시작 ===");
        aiAnalysisCacheService.warmUpTodayAnalysisCache();

        // 1. 발송 메서드 호출
        System.out.println("=== 메일 발송 테스트 시작 ===");
        newsletterBatchService.sendDailyNewsletterPipeline();
        System.out.println("=== 메일 발송 로직 호출 완료 ===");

        // 2. 비동기 스레드 대기 (핵심)
        // newsletterMailService.sendAsync()가 별도의 스레드에서 실행되므로,
        // 메일이 전송되기 전에 테스트(메인 스레드)가 종료되는 것을 방지합니다.
        Thread.sleep(5000); // 5초 대기 (메일이 안 오면 10000으로 늘려보세요)
    }
}