package com.github.jjsh0208.dawncasterbackend.domain.ai.repository;

import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis ,Long> {
    boolean existsByCategoryIdAndAnalysisDate(Long defaultCategoryId, LocalDate today);

    // DISTINCT를 사용하여 Collection Join 시 발생하는 데이터 중복(뻥튀기) 현상 방지
    @Query("SELECT DISTINCT a FROM AiAnalysis a LEFT JOIN FETCH a.impacts WHERE a.analysisDate = :date")
    List<AiAnalysis> findAllByAnalysisDateWithImpacts(@Param("date") LocalDate date);
}
