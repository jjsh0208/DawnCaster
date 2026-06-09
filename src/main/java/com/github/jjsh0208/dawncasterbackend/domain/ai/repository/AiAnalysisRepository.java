package com.github.jjsh0208.dawncasterbackend.domain.ai.repository;

import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis ,Long> {
    boolean existsByCategoryIdAndAnalysisDate(Long defaultCategoryId, LocalDate today);
}
