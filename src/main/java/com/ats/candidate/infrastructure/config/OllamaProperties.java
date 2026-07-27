package com.ats.candidate.infrastructure.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "ollama")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OllamaProperties {
    String baseUrl = "http://localhost:11434";
    String model = "llama3";
    int timeoutSeconds = 60;
    int maxTextLength = 12000;
}
