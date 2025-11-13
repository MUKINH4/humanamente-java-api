package com.humanamente.api.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.humanamente.api.model.Analysis;

import jakarta.persistence.criteria.Predicate;

public class AnalysisSpecification {

    public record AnalysisFilter(
        String userId,
        String jobTitle,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Double minScore,
        Double maxScore
    ) {}

    public static Specification<Analysis> withFilters(AnalysisFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.userId() != null && !filter.userId().isBlank()) {
                predicates.add(
                    cb.equal(root.get("user").get("id"), filter.userId())
                );
            }

            if (filter.jobTitle() != null && !filter.jobTitle().isBlank()) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("jobTitle")), 
                        "%" + filter.jobTitle().toLowerCase() + "%"
                    )
                );
            }

            if (filter.startDate() != null && filter.endDate() != null) {
                predicates.add(
                    cb.between(root.get("analysisDate"), filter.startDate(), filter.endDate())
                );
            }

            if (filter.startDate() != null && filter.endDate() == null) {
                predicates.add(
                    cb.equal(root.get("analysisDate"), filter.startDate())
                );
            }

            if (filter.minScore() != null && filter.maxScore() != null) {
                predicates.add(
                    cb.between(root.get("overallScore"), filter.minScore(), filter.maxScore())
                );
            }

            if (filter.minScore() != null && filter.maxScore() == null) {
                predicates.add(
                    cb.greaterThanOrEqualTo(root.get("overallScore"), filter.minScore())
                );
            }

            if (filter.maxScore() != null && filter.minScore() == null) {
                predicates.add(
                    cb.lessThanOrEqualTo(root.get("overallScore"), filter.maxScore())
                );
            }

            var arrayPredicates = predicates.toArray(new Predicate[0]);
            return cb.and(arrayPredicates);
        };
    }
}
