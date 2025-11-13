package com.humanamente.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisResponse(
    Long id,
    String jobTitle,
    LocalDateTime analysisDate,
    Double overallScore,
    String aiRecommendation,
    List<TaskResponse> tasks
) {
    public record TaskResponse(
        Long id,
        String description,
        Double humanCoreScore,
        String classification,
        String reason,
        List<RecommendationResponse> recommendations
    ) {}
    
    public record RecommendationResponse(
        Long id,
        String upSkill,
        String courseSuggestion,
        String impactLevel
    ) {}
}
