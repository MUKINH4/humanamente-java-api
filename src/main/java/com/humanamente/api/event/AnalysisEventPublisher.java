package com.humanamente.api.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.humanamente.api.config.RabbitMQConfig;
import com.humanamente.api.dto.AnalysisCreatedEvent;
import com.humanamente.api.model.Analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAnalysisCreated(Analysis analysis) {
        AnalysisCreatedEvent event = new AnalysisCreatedEvent(
            analysis.getId(),
            analysis.getUser().getId(),
            analysis.getJobTitle(),
            analysis.getOverallScore(),
            analysis.getAnalysisDate()
        );

        log.info("Publicando evento de análise criada: {}", event);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ANALYSIS_EXCHANGE,
            RabbitMQConfig.ANALYSIS_ROUTING_KEY,
            event
        );
    }
}
