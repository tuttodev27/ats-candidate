package com.ats.candidate.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI candidateOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ATS Candidate API")
                        .version("v1")
                        .description("API para gestion de postulantes y carga de CVs en el modulo ats-candidate.")
                        .contact(new Contact().name("ATS")))
                .servers(List.of(new Server()
                        .url("http://localhost:8084")
                        .description("Local")));
    }
}
