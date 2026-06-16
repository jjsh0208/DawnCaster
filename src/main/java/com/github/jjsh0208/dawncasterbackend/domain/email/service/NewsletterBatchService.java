package com.github.jjsh0208.dawncasterbackend.domain.email.service;

import com.github.jjsh0208.dawncasterbackend.domain.ai.dto.AiAnalysisCacheDto;
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

    // 1. 메인 파이프라인
    @Transactional(readOnly = true)
    public void sendDailyNewsletterPipeline() {
        Cache cache = cacheManager.getCache("dailyAiAnalysis");
        if (cache == null) {
            log.error("Redis 캐시 저장소(dailyAiAnalysis)가 활성화되어 있지 않습니다.");
            return;
        }

        String publishDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        int pageNumber = 0;
        Page<User> userPage;

        do {
            Pageable pageable = PageRequest.of(pageNumber, CHUNK_SIZE);
            userPage = userRepository.findAllByIsDeletedFalse(pageable); // 구독자를 천명 단위로 처리

            for (User user : userPage.getContent()) {
                List<Long> categoryIds = user.getSubscribedCategoryIds(); // 해당 유저가 구독중인 카테고리 추출
                if (categoryIds.isEmpty()) continue;

                // 1. 컨텍스트 조립
                Context context = buildMailContext(categoryIds, cache, publishDateStr); // 이메일에 삽입할 데이터 조립
                if (context == null) continue; // 유효한 데이터가 없을 경우 스킵

                // 2. HTML 렌더링
                String htmlContent = renderHtmlTemplate(context); // 데이터 조립이 끝난 Context로 HTML 렌더링

                // 3. 메일 발송 위임
                dispatchUserMail(user.getEmail(), htmlContent); // 이메일 발송
            }

            pageNumber++;
        } while (userPage.hasNext());

        log.info("전체 구독자 대상 뉴스레터 발송 파이프라인 완료. (총 {} 페이지 처리)", pageNumber);
    }

    // 2. 데이터 가공 및 Thymeleaf Context 조립 로직
    private Context buildMailContext(List<Long> categoryIds, Cache cache, String publishDateStr) {
        List<Map<String, Object>> analysesList = new ArrayList<>();
        String firstCategoryHeadline = "오늘의 한국 증시 브리핑";

        for (Long categoryId : categoryIds) {
            AiAnalysisCacheDto cachedAnalysis = cache.get(categoryId, AiAnalysisCacheDto.class);
            if (cachedAnalysis == null) continue;

            // 첫 번째 카테고리의 제목을 프리헤더로 설정
            if (analysesList.isEmpty()) {
                firstCategoryHeadline = cachedAnalysis.issueTitle();
            }

            Map<String, Object> analysisMap = new HashMap<>();
            analysisMap.put("categoryName", cachedAnalysis.categoryName());
            analysisMap.put("headline", cachedAnalysis.issueTitle());
            analysisMap.put("summary", cachedAnalysis.summary());

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

        if (analysesList.isEmpty()) return null;

        Context context = new Context();
        context.setVariable("mailTitle", "DawnCaster 오늘의 마켓 브리핑");
        context.setVariable("preheader", firstCategoryHeadline);
        context.setVariable("publishDate", publishDateStr);
        context.setVariable("analyses", analysesList);

        return context;
    }

    // 3. 템플릿 엔진을 통한 HTML 렌더링
    private String renderHtmlTemplate(Context context) {
        return templateEngine.process("newsletter/daily-briefing", context);
    }

    // 4. 이메일 발송 컴포넌트
    private void dispatchUserMail(String toEmail, String htmlContent) {
        newsletterMailService.sendAsync(toEmail, "[DawnCaster] 오늘의 브리핑", htmlContent);
    }
}
