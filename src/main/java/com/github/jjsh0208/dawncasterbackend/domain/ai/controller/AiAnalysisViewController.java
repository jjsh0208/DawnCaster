package com.github.jjsh0208.dawncasterbackend.domain.ai.controller;

import com.github.jjsh0208.dawncasterbackend.domain.ai.entity.AiAnalysis;
import com.github.jjsh0208.dawncasterbackend.domain.ai.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/analysis")
public class AiAnalysisViewController {

    private final AiAnalysisService aiAnalysisService;

    @GetMapping("/{categoryId}")
    public String getAnalysisDetail(@PathVariable Long categoryId, Model model) {
        AiAnalysis analysis = aiAnalysisService.getTodayAnalysisByCategoryId(categoryId);
        model.addAttribute("analysis", analysis);
        return "analysis/detail";
    }
}