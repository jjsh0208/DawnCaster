package com.github.jjsh0208.dawncasterbackend.domain.ai.service;

import com.github.jjsh0208.dawncasterbackend.domain.ai.dto.AiAnalysisCacheDto;
import com.github.jjsh0208.dawncasterbackend.domain.email.service.NewsletterMailService;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import com.github.jjsh0208.dawncasterbackend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterBatchService {

    private final UserRepository userRepository;
    private final RedisCacheManager cacheManager;
    private final SpringTemplateEngine templateEngine;
    private final NewsletterMailService newsletterMailService;

    private static final int CHUNK_SIZE = 1000;

    @Transactional(readOnly = true)
    public void sendDailyNewsletterPipeline() {
        int pageNumber = 0;
        Page<User> userPage;

        Cache cache = cacheManager.getCache("dailyAiAnalysis");
        if (cache == null) {
            log.error("Redis 캐시 저장소(dailyAiAnalysis)가 활성화되어 있지 않습니다.");
            return;
        }

        String publishDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        do {
            Pageable pageable = PageRequest.of(pageNumber, CHUNK_SIZE);
            userPage = userRepository.findAllByIsDeletedFalse(pageable);

            for (User user : userPage.getContent()) {
                List<Map<String, Object>> analysesList = new ArrayList<>();
                String firstCategoryHeadline = "오늘의 한국 증시 브리핑";

                // default_batch_fetch_size 작동: 1,000명 유저의 구독 정보가 1번의 IN 절 쿼리로 일괄 로드됨
                List<Long> categoryIds = user.getSubscribedCategoryIds();
                if (categoryIds.isEmpty()) continue;

                for (Long categoryId : categoryIds) {
                    AiAnalysisCacheDto cachedAnalysis = cache.get(categoryId, AiAnalysisCacheDto.class);
                    if (cachedAnalysis == null) continue;

                    if (analysesList.isEmpty()) {
                        firstCategoryHeadline = cachedAnalysis.issueTitle();
                    }

                    // 카테고리 레벨 데이터 매핑
                    Map<String, Object> analysisMap = new HashMap<>();
                    analysisMap.put("categoryName", cachedAnalysis.categoryName());
                    analysisMap.put("headline", cachedAnalysis.issueTitle());
                    analysisMap.put("summary", cachedAnalysis.summary());

                    // 하위 섹터 임팩트 데이터 매핑
                    List<Map<String, Object>> impactList = new ArrayList<>();
                    for (AiAnalysisCacheDto.ImpactCacheDto impact : cachedAnalysis.impacts()) {
                        Map<String, Object> impactMap = new HashMap<>();
                        impactMap.put("name", impact.sectorName());
                        impactMap.put("analysis", impact.reasoning());
                        impactMap.put("relatedStocks", impact.relatedStocks());

                        String impactType = impact.impactType();
                        impactMap.put("statusText", impactType.equals("POSITIVE") ? "긍정적" : "부정적");
                        impactMap.put("colorCode", impactType.equals("POSITIVE") ? "#10B981" : "#EF4444");

                        impactList.add(impactMap);
                    }

                    analysisMap.put("impacts", impactList);
                    analysesList.add(analysisMap);
                }

                if (analysesList.isEmpty()) continue;

                // 타임리프 컨텍스트 구성
                Context context = new Context();
                context.setVariable("mailTitle", "DawnCaster 오늘의 마켓 브리핑");
                context.setVariable("preheader", firstCategoryHeadline);
                context.setVariable("publishDate", publishDateStr);
                context.setVariable("analyses", analysesList);

                // HTML 템플릿 컴파일 (newsletter/daily-briefing.html)
                String htmlContent = templateEngine.process("newsletter/daily-briefing", context);

                // 3단계 비동기 발송 호출
                newsletterMailService.sendAsync(user.getEmail(), "[DawnCaster] 오늘의 브리핑", htmlContent);
            }

            pageNumber++;
        } while (userPage.hasNext());

        log.info("전체 구독자 대상 뉴스레터 발송 파이프라인 완료. (총 {} 페이지 처리)", pageNumber);
    }
}
