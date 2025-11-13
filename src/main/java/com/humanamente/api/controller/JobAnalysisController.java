package com.humanamente.api.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanamente.api.dto.AnalysisResponse;
import com.humanamente.api.dto.JobAnalysisRequest;
import com.humanamente.api.model.Analysis;
import com.humanamente.api.model.Task;
import com.humanamente.api.model.User;
import com.humanamente.api.service.AnalysisService;
import com.humanamente.api.service.IAService;
import com.humanamente.api.service.MessageService;
import com.humanamente.api.service.RecommendationService;
import com.humanamente.api.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/job-analysis")
@RequiredArgsConstructor
public class JobAnalysisController {
    
    private final IAService iaService;
    private final AnalysisService analysisService;
    private final RecommendationService recommendationService;
    private final UserService userService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyzeJob(
            @RequestBody @Valid JobAnalysisRequest request,
            @AuthenticationPrincipal User authenticatedUser) {
        
        // Busca o usuário completo do banco de dados usando o ID do usuário autenticado
        User user = userService.findById(authenticatedUser.getId());
        
        // Chama a IA para analisar o trabalho e gerar as tasks
        Analysis analysis = iaService.analyzeJobWithTasks(
            request.jobTitle(), 
            request.jobDescription(), 
            user
        );
        
        // Salva a análise com as tasks
        Analysis savedAnalysis = analysisService.create(analysis);
        
        // Gera recomendações para cada task
        for (Task task : savedAnalysis.getTasks()) {
            var recommendations = iaService.generateRecommendations(task);
            recommendations.forEach(rec -> {
                rec.setTask(task);
                recommendationService.create(rec);
            });
        }
        
        // IMPORTANTE: Recarrega a análise DEPOIS de criar as recomendações
        Analysis completeAnalysis = analysisService.findById(savedAnalysis.getId());
        
        // Converte para DTO de resposta
        AnalysisResponse response = toAnalysisResponse(completeAnalysis);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/task/{taskId}/recommendations")
    public ResponseEntity<?> generateTaskRecommendations(@PathVariable Long taskId) {
        
        var taskService = analysisService.findById(taskId);
        
        // Encontra a task dentro da análise
        Task task = taskService.getTasks().stream()
            .filter(t -> t.getId().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.task.not.found", taskId)));
        
        // Gera novas recomendações
        var recommendations = iaService.generateRecommendations(task);
        recommendations.forEach(rec -> {
            rec.setTask(task);
            recommendationService.create(rec);
        });
        
        return ResponseEntity.ok(recommendations);
    }
    
    private AnalysisResponse toAnalysisResponse(Analysis analysis) {
        return new AnalysisResponse(
            analysis.getId(),
            analysis.getJobTitle(),
            analysis.getAnalysisDate(),
            analysis.getOverallScore(),
            analysis.getAiRecommendation(),
            analysis.getTasks().stream()
                .map(task -> new AnalysisResponse.TaskResponse(
                    task.getId(),
                    task.getDescription(),
                    task.getHumanCoreScore(),
                    task.getClassification().toString(),
                    task.getReason(),
                    task.getRecommendations().stream()
                        .map(rec -> new AnalysisResponse.RecommendationResponse(
                            rec.getId(),
                            rec.getUpSkill(),
                            rec.getCourseSuggestion(),
                            rec.getImpactLevel().toString()
                        ))
                        .collect(Collectors.toList())
                ))
                .collect(Collectors.toList())
        );
    }
}
