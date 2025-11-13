package com.humanamente.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanamente.api.model.Analysis;
import com.humanamente.api.model.Recommendation;
import com.humanamente.api.model.Task;
import com.humanamente.api.model.User;
import com.humanamente.api.model.enums.Classification;
import com.humanamente.api.model.enums.ImpactLevel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IAService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String groqModel;

    public IAService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Analysis analyzeJobWithTasks(String jobTitle, String jobDescription, User user) {
        try {
            validateApiKey();
            
            String prompt = buildAnalysisPrompt(jobTitle, jobDescription);
            String iaResponse = callGroqAPI(prompt);
            
            return parseAnalysisResponse(iaResponse, jobTitle, user);
            
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("API Key inválida ou não configurada");
            throw new RuntimeException("Erro de autenticação com a API Groq. Verifique sua API Key.", e);
        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP ao chamar API Groq: {} - {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Erro ao comunicar com a API Groq: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro ao analisar trabalho com IA: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar análise de IA", e);
        }
    }

    public List<Recommendation> generateRecommendations(Task task) {
        try {
            validateApiKey();
            
            String prompt = buildRecommendationPrompt(task);
            String iaResponse = callGroqAPI(prompt);
            
            return parseRecommendations(iaResponse, task);
            
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("API Key inválida ou não configurada");
            throw new RuntimeException("Erro de autenticação com a API Groq. Verifique sua API Key.", e);
        } catch (Exception e) {
            log.error("Erro ao gerar recomendações: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar recomendações", e);
        }
    }

    private void validateApiKey() {
        if (groqApiKey == null || groqApiKey.isEmpty()) {
            log.error("Groq API Key não configurada.");
            throw new RuntimeException("Groq API Key não configurada. Configure a chave no application.properties");
        }
        
        // Log mascarado para segurança (mostra apenas primeiros e últimos caracteres)
        String maskedKey = groqApiKey.length() > 10 
            ? groqApiKey.substring(0, 7) + "..." + groqApiKey.substring(groqApiKey.length() - 4)
            : "***";
        log.info("API Key configurada: {} (length: {})", maskedKey, groqApiKey.length());
        
        // Verifica se a key começa com o prefixo correto
        if (!groqApiKey.startsWith("gsk_")) {
            log.error("API Key não parece ter o formato correto. Deve começar com 'gsk_'");
            throw new RuntimeException("API Key do Groq em formato inválido");
        }
    }

    private String buildAnalysisPrompt(String jobTitle, String jobDescription) {
        return String.format(
            "Você é um especialista em análise de trabalho e automação. " +
            "Analise o seguinte cargo: %s\n\n" +
            "Descrição: %s\n\n" +
            "Retorne APENAS um JSON válido com a seguinte estrutura:\n" +
            "{\n" +
            "  \"overallScore\": 75.5,\n" +
            "  \"aiRecommendation\": \"texto aqui\",\n" +
            "  \"tasks\": [\n" +
            "    {\n" +
            "      \"description\": \"texto\",\n" +
            "      \"humanCoreScore\": 80.0,\n" +
            "      \"classification\": \"HUMAN\",\n" +
            "      \"reason\": \"texto\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n\n" +
            "Classifique cada tarefa como:\n" +
            "- HUMAN: tarefas que exigem criatividade, empatia, julgamento crítico\n" +
            "- HYBRID: tarefas que podem ser auxiliadas por IA mas precisam supervisão humana\n" +
            "- AUTOMATED: tarefas repetitivas que podem ser totalmente automatizadas",
            jobTitle, jobDescription
        );
    }

    private String buildRecommendationPrompt(Task task) {
        return String.format(
            "Para a seguinte tarefa classificada como %s:\n" +
            "Descrição: %s\n" +
            "Razão: %s\n" +
            "Score Humano: %.2f\n\n" +
            "Gere 3 recomendações de upskilling em formato JSON.\n" +
            "Retorne APENAS um JSON válido com a estrutura:\n" +
            "{\n" +
            "  \"recommendations\": [\n" +
            "    {\n" +
            "      \"upSkill\": \"texto\",\n" +
            "      \"courseSuggestion\": \"texto\",\n" +
            "      \"impactLevel\": \"HIGH\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n\n" +
            "ImpactLevel deve ser: HIGH, MEDIUM ou LOW",
            task.getClassification(), task.getDescription(), task.getReason(), task.getHumanCoreScore()
        );
    }

    private String callGroqAPI(String prompt) {
        log.info("Chamando API Groq com modelo: {}", groqModel);
        log.info("URL da API: {}", groqApiUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Adiciona o Bearer token (certifica que não há espaços extras)
        String cleanApiKey = groqApiKey.trim();
        headers.set("Authorization", "Bearer " + cleanApiKey);
        
        log.debug("Authorization header configurado");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqModel);
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", "Você é uma IA que ajuda a analisar profissões. Sempre retorne respostas em JSON válido."),
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Enviando requisição para Groq API...");
            
            ResponseEntity<String> response = restTemplate.exchange(
                groqApiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            log.info("Resposta recebida com status: {}", response.getStatusCode());
            return extractContentFromResponse(response.getBody());
            
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Erro 401: API Key inválida ou expirada");
            log.error("Response body: {}", e.getResponseBodyAsString());
            throw new RuntimeException("API Key do Groq inválida ou expirada. Gere uma nova em https://console.groq.com/keys", e);
        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP {}: {}", e.getStatusCode(), e.getMessage());
            log.error("Response body: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao chamar Groq API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar Groq API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao chamar Groq API: " + e.getMessage(), e);
        }
    }

    private String extractContentFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            
            if (choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).path("message").path("content").asText();
                
                // Remove markdown se existir
                content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
                
                log.info("Conteúdo extraído da IA: {}", content);
                return content;
            }
            
            throw new RuntimeException("Resposta da IA em formato inválido");
        } catch (Exception e) {
            log.error("Erro ao extrair conteúdo da resposta: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar resposta da IA", e);
        }
    }

    private Analysis parseAnalysisResponse(String iaResponse, String jobTitle, User user) throws Exception {
        JsonNode responseJson = objectMapper.readTree(iaResponse);
        
        Analysis analysis = Analysis.builder()
            .user(user)
            .jobTitle(jobTitle)
            .overallScore(responseJson.path("overallScore").asDouble(0.0))
            .aiRecommendation(responseJson.path("aiRecommendation").asText("Não disponível"))
            .tasks(new ArrayList<>())
            .build();

        JsonNode tasksNode = responseJson.path("tasks");
        if (tasksNode.isArray()) {
            for (JsonNode taskNode : tasksNode) {
                Task task = Task.builder()
                    .analysis(analysis)
                    .description(taskNode.path("description").asText("Não especificado"))
                    .humanCoreScore(taskNode.path("humanCoreScore").asDouble(0.0))
                    .classification(Classification.valueOf(taskNode.path("classification").asText("HYBRID")))
                    .reason(taskNode.path("reason").asText("Não especificado"))
                    .recommendations(new ArrayList<>())
                    .build();
                
                analysis.getTasks().add(task);
            }
        }

        return analysis;
    }

    private List<Recommendation> parseRecommendations(String iaResponse, Task task) throws Exception {
        JsonNode responseJson = objectMapper.readTree(iaResponse);
        List<Recommendation> recommendations = new ArrayList<>();

        JsonNode recsNode = responseJson.path("recommendations");
        if (recsNode.isArray()) {
            for (JsonNode recNode : recsNode) {
                String impactLevelStr = recNode.path("impactLevel").asText("MEDIUM");
                
                Recommendation recommendation = Recommendation.builder()
                    .task(task)
                    .upSkill(recNode.path("upSkill").asText("Não especificado"))
                    .courseSuggestion(recNode.path("courseSuggestion").asText("Não especificado"))
                    .impactLevel(ImpactLevel.valueOf(impactLevelStr))
                    .build();
                
                recommendations.add(recommendation);
            }
        }

        return recommendations;
    }
}