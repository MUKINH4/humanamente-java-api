package com.humanamente.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.humanamente.api.model.Analysis;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long>, JpaSpecificationExecutor<Analysis> {
    
    List<Analysis> findByUserId(String userId);
    
    List<Analysis> findByUserIdOrderByAnalysisDateDesc(String userId);
    
    List<Analysis> findByAnalysisDateBetween(LocalDateTime start, LocalDateTime end);
}
