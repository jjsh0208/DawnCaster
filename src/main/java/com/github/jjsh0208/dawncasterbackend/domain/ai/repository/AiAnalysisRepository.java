package com.github.jjsh0208.dawncasterbackend.domain.ai.repository;

import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis ,Long> {
    boolean existsByCategoryIdAndAnalysisDate(Long defaultCategoryId, LocalDate today);

    // 카테고리 ID와 날짜를 기준으로 하위 Impact 엔티티까지 한 번에 조회 (N+1 방지)
    @Query("SELECT a FROM AiAnalysis a LEFT JOIN FETCH a.impacts WHERE a.categoryId = :categoryId AND a.analysisDate = :date")
    Optional<AiAnalysis> findByCategoryIdAndAnalysisDateWithImpacts(@Param("categoryId") Long categoryId, @Param("date") LocalDate date);
}
