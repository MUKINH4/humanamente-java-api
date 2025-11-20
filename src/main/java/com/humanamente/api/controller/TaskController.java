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

import com.humanamente.api.model.Task;
import com.humanamente.api.model.enums.Classification;
import com.humanamente.api.service.TaskService;
import com.humanamente.api.dto.ApiResponse;
import com.humanamente.api.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    private final TaskService taskService;
    private final MessageService messageService;

    public TaskController(TaskService taskService, MessageService messageService) {
        this.taskService = taskService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Task>> create(@RequestBody @Valid Task task) {
        try {
            Task created = taskService.create(task);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.getMessage("success.task.created"), created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> findById(@PathVariable Long id) {
        try {
            Task task = taskService.findById(id);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.task.found"), task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> findAll() {
        try {
            List<Task> tasks = taskService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.tasks.found"), tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<ApiResponse<List<Task>>> findByAnalysisId(@PathVariable Long analysisId) {
        try {
            List<Task> tasks = taskService.findByAnalysisId(analysisId);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.tasks.found"), tasks));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping("/classification")
    public ResponseEntity<ApiResponse<List<Task>>> findByClassification(@RequestParam Classification classification) {
        try {
            List<Task> tasks = taskService.findByClassification(classification);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.tasks.found"), tasks));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> update(@PathVariable Long id, @RequestBody @Valid Task task) {
        try {
            Task updated = taskService.update(id, task);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.task.updated"), updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.task.deleted"), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }
}
