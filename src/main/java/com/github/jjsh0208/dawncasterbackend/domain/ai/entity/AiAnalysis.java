package com.github.jjsh0208.dawncasterbackend.domain.ai.entity;

import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_analysis")
@Getter
@NoArgsConstructor
public class AiAnalysis extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "issue_title", nullable = false)
    private String issueTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @OneToMany(mappedBy = "aiAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiAnalysisImpact> impacts = new ArrayList<>();

    @Builder
    public AiAnalysis(Long categoryId, LocalDate analysisDate, String issueTitle, String summary){
        this.categoryId = categoryId;
        this.analysisDate = analysisDate;
        this.issueTitle = issueTitle;
        this.summary = summary;
    }


    public void addImpact(AiAnalysisImpact impact) {
        this.impacts.add(impact);
        impact.setAiAnalysis(this);
    }
}