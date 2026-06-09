package com.github.jjsh0208.dawncasterbackend.domain.ai.entity;

import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.enums.ImpactType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_analysis_impacts")
@Getter
@NoArgsConstructor
public class AiAnalysisImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AiAnalysis aiAnalysis;

    @Column(name = "sector_name", nullable = false, length = 100)
    private String sectorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "impact_type", nullable = false, length = 20)
    private ImpactType impactType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reasoning;

    @Column(name = "related_stocks")
    private String relatedStocks;

    protected void setAiAnalysis(AiAnalysis aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public AiAnalysisImpact(String sectorName, ImpactType impactType, String reasoning, String relatedStocks) {
        this.sectorName = sectorName;
        this.impactType = impactType;
        this.reasoning = reasoning;
        this.relatedStocks = relatedStocks;
    }
}