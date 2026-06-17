package com.github.jjsh0208.dawncasterbackend.global.scheduler;

import com.github.jjsh0208.dawncasterbackend.domain.email.entity.EmailSendHistory;
import com.github.jjsh0208.dawncasterbackend.domain.email.enums.SendStatus;
import com.github.jjsh0208.dawncasterbackend.domain.email.repository.EmailSendHistoryRepository;
import com.github.jjsh0208.dawncasterbackend.domain.email.service.NewsletterBatchService;
import com.github.jjsh0208.dawncasterbackend.domain.email.service.NewsletterMailService;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRedeliveryScheduler {

    private final EmailSendHistoryRepository emailSendHistoryRepository;
    private final NewsletterBatchService newsletterBatchService;
    private final NewsletterMailService newsletterMailService;
    private final RedisCacheManager cacheManager;


    /**
     * 메인 배치가 오전 5시에 돌고 난 후,
     * 매시간 30분마다 당일 발송 실패 건(FAIL)을 스캔하여 재발송 시도
     */
    @Scheduled(cron = "0 30 * * * *")
    @Transactional
    public void executeRedeliveryPipeline() {
        LocalDate today = LocalDate.now();
        List<EmailSendHistory> failedHistories = emailSendHistoryRepository
                .findBySendStatusAndSendDate(SendStatus.FAIL, today);

        if (failedHistories.isEmpty()) {
            return;
        }

        Cache cache = cacheManager.getCache("dailyAiAnalysis");
        if (cache == null) {
            log.error("[Mail Redelivery] Redis 캐시가 비어있어 재발송을 중단합니다.");
            return;
        }

        log.info("[Mail Redelivery] 오늘 발송 실패한 메일 {}건 재발송 처리 시작", failedHistories.size());
        String publishDateStr = today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        for (EmailSendHistory history : failedHistories) {
            User user = history.getUser();
            try {
                // 누락되었던 HTML 본문을 실시간으로 다시 생성
                String htmlContent = newsletterBatchService.generateHtmlForUser(user, cache, publishDateStr);

                if (htmlContent != null) {
                    // 동기(Sync) 발송 시도
                    newsletterMailService.redeliverSync(user.getEmail(), "[DawnCaster] 오늘의 마켓 브리핑", htmlContent);
                    // 에러 발생 없이 통과했다면 영속성 상태 업데이트 (JPA Dirty Checking 반영)
                    history.markAsSuccess();
                }
            } catch (Exception e) {
                log.error("[Mail Redelivery Error] 유저 {} 재발송 2차 실패: {}", user.getEmail(), e.getMessage());
                // 실패 원인 갱신
                history.updateFailReason(e.getMessage());
            }
        }
    }
}