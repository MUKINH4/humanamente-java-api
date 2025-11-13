package com.humanamente.api.model;

import com.humanamente.api.model.enums.ImpactLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Recommendation {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    @NotNull(message = "Task is required")
    @JsonIgnoreProperties({"recommendations", "analysis"})
    private Task task;

    @Column(name = "up_skill", length = 150)
    @NotBlank(message = "Upskill is required")
    private String upSkill;

    @Column(name = "course_suggestion", length = 200)
    @NotBlank(message = "Course suggestion is required")
    private String courseSuggestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "impact_level", length = 20)
    @NotNull(message = "Impact level is required")
    private ImpactLevel impactLevel;
}
