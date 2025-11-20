package com.humanamente.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanamente.api.model.Analysis;
import com.humanamente.api.service.AnalysisService;
import com.humanamente.api.specification.AnalysisSpecification.AnalysisFilter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    
    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<Analysis> create(@RequestBody @Valid Analysis analysis) {
        Analysis created = analysisService.create(analysis);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Analysis> findById(@PathVariable Long id) {
        Analysis analysis = analysisService.findById(id);
        if (analysis == null) {
            return ResponseEntity.notFound().build();
        };
        return ResponseEntity.ok(analysis);
    }

    @GetMapping
    public ResponseEntity<Page<Analysis>> findAll(
            AnalysisFilter filter,
            @PageableDefault(size = 10, sort = "analysisDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Analysis> analyses = analysisService.findWithFilters(filter, pageable);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Analysis>> findByUserId(@PathVariable String userId) {
        List<Analysis> analyses = analysisService.findByUserIdOrderByDate(userId);
        if (analyses == null || analyses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analyses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Analysis> update(@PathVariable Long id, @RequestBody @Valid Analysis analysis) {
        if (!id.equals(analysis.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Analysis updated = analysisService.update(id, analysis);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (analysisService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        analysisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
