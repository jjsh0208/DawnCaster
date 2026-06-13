package com.github.jjsh0208.dawncasterbackend.domain.ai.dto;

import java.util.List;

// 캐싱용 DTO
public record AiAnalysisCacheDto(
        Long categoryId,
        String categoryName,
        String issueTitle,
        String summary,
        List<ImpactCacheDto> impacts
) {
    public record ImpactCacheDto(
            String sectorName,
            String impactType,
            String reasoning,
            String relatedStocks
    ) {}
}