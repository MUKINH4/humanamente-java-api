package com.humanamente.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanamente.api.model.Recommendation;
import com.humanamente.api.model.enums.ImpactLevel;
import com.humanamente.api.service.RecommendationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<Recommendation> create(@RequestBody @Valid Recommendation recommendation) {
        Recommendation created = recommendationService.create(recommendation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recommendation> findById(@PathVariable Long id) {
        Recommendation recommendation = recommendationService.findById(id);
        return ResponseEntity.ok(recommendation);
    }

    @GetMapping
    public ResponseEntity<List<Recommendation>> findAll() {
        List<Recommendation> recommendations = recommendationService.findAll();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Recommendation>> findByTaskId(@PathVariable Long taskId) {
        List<Recommendation> recommendations = recommendationService.findByTaskId(taskId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/impact-level")
    public ResponseEntity<List<Recommendation>> findByImpactLevel(@RequestParam ImpactLevel impactLevel) {
        List<Recommendation> recommendations = recommendationService.findByImpactLevel(impactLevel);
        return ResponseEntity.ok(recommendations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recommendation> update(@PathVariable Long id, @RequestBody @Valid Recommendation recommendation) {
        Recommendation updated = recommendationService.update(id, recommendation);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
