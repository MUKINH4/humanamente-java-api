package com.humanamente.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanamente.api.model.Analysis;
import com.humanamente.api.repository.AnalysisRepository;
import com.humanamente.api.specification.AnalysisSpecification;
import com.humanamente.api.specification.AnalysisSpecification.AnalysisFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {
    
    private final AnalysisRepository analysisRepository;
    private final MessageService messageService;
    
    @Transactional
    @CacheEvict(value = "analyses", allEntries = true)
    public Analysis create(Analysis analysis) {
        log.info("Criando análise para usuário: {}", analysis.getUser().getId());
        
        if (analysis.getTasks() != null) {
            analysis.getTasks().forEach(task -> task.setAnalysis(analysis));
        }
        
        Analysis saved = analysisRepository.save(analysis);
        log.info("Análise criada com ID: {}", saved.getId());
        
        return saved;
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "analyses", key = "#id")
    public Analysis findById(Long id) {
        log.info("Buscando análise do banco (não está em cache): {}", id);
        
        Analysis analysis = analysisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.analysis.not.found", id)));
        
        if (analysis.getTasks() != null) {
            analysis.getTasks().size();
            analysis.getTasks().forEach(task -> {
                if (task.getRecommendations() != null) {
                    task.getRecommendations().size();
                }
            });
        }
        
        return analysis;
    }
    
    @Transactional(readOnly = true)
    public Page<Analysis> findWithFilters(AnalysisFilter filter, Pageable pageable) {
        log.info("Buscando análises com filtros: {}", filter);
        return analysisRepository.findAll(AnalysisSpecification.withFilters(filter), pageable);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "analyses", key = "'user_' + #userId")
    public List<Analysis> findByUserId(String userId) {
        log.info("Buscando análises do usuário {} do banco (não está em cache)", userId);
        return analysisRepository.findByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<Analysis> findByUserIdOrderByDate(String userId) {
        return analysisRepository.findByUserIdOrderByAnalysisDateDesc(userId);
    }
    
    @Transactional(readOnly = true)
    public List<Analysis> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return analysisRepository.findByAnalysisDateBetween(start, end);
    }
    
    @Transactional
    @CacheEvict(value = "analyses", allEntries = true)
    public Analysis update(Long id, Analysis analysis) {
        log.info("Atualizando análise {} - limpando cache", id);
        
        Analysis existing = findById(id);
        existing.setJobTitle(analysis.getJobTitle());
        existing.setOverallScore(analysis.getOverallScore());
        existing.setAiRecommendation(analysis.getAiRecommendation());
        
        if (analysis.getTasks() != null) {
            existing.getTasks().clear();
            analysis.getTasks().forEach(task -> {
                task.setAnalysis(existing);
                existing.getTasks().add(task);
            });
        }
        
        return analysisRepository.save(existing);
    }
    
    @Transactional
    @CacheEvict(value = "analyses", allEntries = true)
    public void delete(Long id) {
        log.info("Deletando análise {} - limpando cache", id);
        analysisRepository.deleteById(id);
    }
}
