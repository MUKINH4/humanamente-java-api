package com.humanamente.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanamente.api.model.Task;
import com.humanamente.api.model.enums.Classification;
import com.humanamente.api.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    @Transactional
    public Task create(Task task) {
        if (task.getRecommendations() != null) {
            task.getRecommendations().forEach(rec -> rec.setTask(task));
        }
        return taskRepository.save(task);
    }
    
    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
    
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
    
    public List<Task> findByAnalysisId(Long analysisId) {
        return taskRepository.findByAnalysisId(analysisId);
    }
    
    public List<Task> findByClassification(Classification classification) {
        return taskRepository.findByClassification(classification);
    }
    
    @Transactional
    public Task update(Long id, Task task) {
        Task existing = findById(id);
        existing.setDescription(task.getDescription());
        existing.setHumanCoreScore(task.getHumanCoreScore());
        existing.setClassification(task.getClassification());
        existing.setReason(task.getReason());
        
        if (task.getRecommendations() != null) {
            existing.getRecommendations().clear();
            task.getRecommendations().forEach(rec -> {
                rec.setTask(existing);
                existing.getRecommendations().add(rec);
            });
        }
        
        return taskRepository.save(existing);
    }
    
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
