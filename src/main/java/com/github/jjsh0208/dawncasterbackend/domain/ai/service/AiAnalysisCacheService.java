package com.github.jjsh0208.dawncasterbackend.domain.ai.service;

import com.github.jjsh0208.dawncasterbackend.domain.ai.dto.AiAnalysisCacheDto;
import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysis;
import com.github.jjsh0208.dawncasterbackend.domain.ai.repository.AiAnalysisRepository;
import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisCacheService {

    private final AiAnalysisRepository aiAnalysisRepository;
    private final RedisCacheManager cacheManager;
    private final CategoryRepository categoryRepository;

    private static final String CACHE_NAME = "dailyAiAnalysis";

    @Transactional(readOnly = true)
    public void warmUpTodayAnalysisCache() {
        LocalDate today = LocalDate.now();
        List<AiAnalysis> todayAnalyses = aiAnalysisRepository.findAllByAnalysisDateWithImpacts(today);

        if (todayAnalyses.isEmpty()) {
            log.warn("오늘({}) 날짜의 AI 분석 데이터가 없습니다. 캐싱을 건너뜁니다.", today);
            return;
        }

        // 카테고리 전체를 조회하여 ID-이름 매핑 맵 생성 (In-Memory Join 준비)
        Map<Long, String> categoryNameMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            log.error("Redis Cache [{}]를 찾을 수 없습니다. CacheConfig 설정을 확인하세요.", CACHE_NAME);
            return;
        }

        // 1. 기존 캐시 초기화 (어제 데이터 등 찌꺼기 제거)
        cache.clear();

        // 2. 엔티티 -> DTO 변환 후 Redis에 적재 (Key: CategoryId)
        for (AiAnalysis analysis : todayAnalyses) {
            String categoryName = categoryNameMap.getOrDefault(analysis.getCategoryId(), "미분류");
            AiAnalysisCacheDto dto = convertToDto(analysis,categoryName);
            cache.put(analysis.getCategoryId(), dto);
        }

        log.info("Redis 캐시 워밍 완료: 총 {}개의 카테고리 분석 데이터 적재", todayAnalyses.size());
    }

    // 엔티티를 안전한 DTO로 매핑하는 내부 헬퍼 메서드
    private AiAnalysisCacheDto convertToDto(AiAnalysis analysis, String categoryName) {
        List<AiAnalysisCacheDto.ImpactCacheDto> impactDtos = analysis.getImpacts().stream()
                .map(impact -> new AiAnalysisCacheDto.ImpactCacheDto(
                        impact.getSectorName(),
                        impact.getImpactType().name(), // Enum은 String으로 변환
                        impact.getReasoning(),
                        impact.getRelatedStocks()
                ))
                .collect(Collectors.toList());

        return new AiAnalysisCacheDto(
                analysis.getCategoryId(),
                categoryName,
                analysis.getIssueTitle(),
                analysis.getSummary(),
                impactDtos
        );
    }
}
