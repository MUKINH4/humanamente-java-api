package com.humanamente.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanamente.api.model.Recommendation;
import com.humanamente.api.model.enums.ImpactLevel;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    
    List<Recommendation> findByTaskId(Long taskId);
    
    List<Recommendation> findByImpactLevel(ImpactLevel impactLevel);
    
}
