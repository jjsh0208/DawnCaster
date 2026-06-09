package com.github.jjsh0208.dawncasterbackend.domain.ai.entity;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void addImpact(AiAnalysisImpact impact) {
        this.impacts.add(impact);
        impact.setAiAnalysis(this);
    }
}