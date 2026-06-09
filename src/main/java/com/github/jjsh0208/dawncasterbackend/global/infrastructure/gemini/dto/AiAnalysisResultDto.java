package com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.dto;

import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.enums.ImpactType;
import lombok.Data;

import java.util.List;

@Data
public class AiAnalysisResultDto {
    private String email_subject;
    private String issue_title;
    private String summary;
    private List<ImpactDto> impacts;

    @Data
    public static class ImpactDto {
        private String sector_name;
        private ImpactType impact_type;
        private String reasoning;
        private String related_stocks;
    }
}