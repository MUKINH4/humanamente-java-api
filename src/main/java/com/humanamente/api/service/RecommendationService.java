package com.humanamente.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.humanamente.api.model.Recommendation;
import com.humanamente.api.model.enums.ImpactLevel;
import com.humanamente.api.repository.RecommendationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    
    private final RecommendationRepository recommendationRepository;
    
    public Recommendation create(Recommendation recommendation) {
        return recommendationRepository.save(recommendation);
    }
    
    public Recommendation findById(Long id) {
        return recommendationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + id));
    }
    
    public List<Recommendation> findAll() {
        return recommendationRepository.findAll();
    }
    
    public List<Recommendation> findByTaskId(Long taskId) {
        return recommendationRepository.findByTaskId(taskId);
    }
    
    public List<Recommendation> findByImpactLevel(ImpactLevel impactLevel) {
        return recommendationRepository.findByImpactLevel(impactLevel);
    }
    
    public Recommendation update(Long id, Recommendation recommendation) {
        Recommendation existing = findById(id);
        existing.setUpSkill(recommendation.getUpSkill());
        existing.setCourseSuggestion(recommendation.getCourseSuggestion());
        existing.setImpactLevel(recommendation.getImpactLevel());
        return recommendationRepository.save(existing);
    }
    
    public void delete(Long id) {
        recommendationRepository.deleteById(id);
    }
}
