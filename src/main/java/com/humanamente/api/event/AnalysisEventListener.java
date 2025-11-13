package com.humanamente.api.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.humanamente.api.config.RabbitMQConfig;
import com.humanamente.api.dto.AnalysisCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AnalysisEventListener {

    @RabbitListener(queues = RabbitMQConfig.ANALYSIS_QUEUE)
    public void handleAnalysisCreated(AnalysisCreatedEvent event) {
        log.info("Evento recebido: Nova análise criada - ID: {}, Usuário: {}, Job: {}", 
            event.analysisId(), event.userId(), event.jobTitle());
        
        // Aqui você pode:
        // - Enviar email de notificação
        // - Atualizar estatísticas
        // - Processar dados assíncronos
        // - Integrar com outros sistemas
    }
}
