package com.humanamente.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanamente.api.model.Task;
import com.humanamente.api.model.enums.Classification;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByAnalysisId(Long analysisId);
    
    List<Task> findByClassification(Classification classification);
    
}
