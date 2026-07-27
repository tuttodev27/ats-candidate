package com.ats.candidate.infrastructure.out.ollama;

import com.ats.candidate.infrastructure.config.OllamaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OllamaClient {

    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);

    private final RestClient restClient;
    private final OllamaProperties properties;
    private final ObjectMapper objectMapper;

    public OllamaClient(OllamaProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String chat(String systemPrompt, String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", properties.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "stream", false,
                "format", "json"
        );

        log.info("Calling Ollama model={} at {}", properties.getModel(), properties.getBaseUrl());

        String rawResponse = restClient.post()
                .uri("/api/chat")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractContent(rawResponse);
    }

    public boolean isAvailable() {
        try {
            restClient.get()
                    .uri("/api/tags")
                    .retrieve()
                    .body(String.class);
            return true;
        } catch (Exception ex) {
            log.warn("Ollama not available at {}: {}", properties.getBaseUrl(), ex.getMessage());
            return false;
        }
    }

    private String extractContent(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            return root.path("message").path("content").asText();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse Ollama response: " + ex.getMessage(), ex);
        }
    }
}
