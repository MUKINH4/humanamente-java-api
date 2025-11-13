package com.humanamente.api.dto;

import jakarta.validation.constraints.NotBlank;

public record JobAnalysisRequest(
    @NotBlank(message = "Job title is required")
    String jobTitle,
    
    @NotBlank(message = "Job description is required")
    String jobDescription
) {}
