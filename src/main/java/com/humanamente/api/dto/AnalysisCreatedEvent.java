package com.humanamente.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AnalysisCreatedEvent(
    Long analysisId,
    String userId,
    String jobTitle,
    Double overallScore,
    LocalDateTime analysisDate
) implements Serializable {}
