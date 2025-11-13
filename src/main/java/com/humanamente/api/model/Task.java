package com.humanamente.api.model;

import java.util.List;

import com.humanamente.api.model.enums.Classification;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
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
public class Task {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "analysis_id")
    @ManyToOne
    @NotNull(message = "Analysis is required")
    @JsonIgnoreProperties({"tasks", "user"})
    private Analysis analysis;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Recommendation> recommendations;  
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Description is required")
    private String description;

    private Double humanCoreScore;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotNull(message = "Classification is required")
    private Classification classification;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
}
