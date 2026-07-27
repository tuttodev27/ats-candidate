package com.ats.candidate;

import com.ats.candidate.infrastructure.config.OllamaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OllamaProperties.class)
public class AtsCandidateApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtsCandidateApplication.class, args);
	}

}
