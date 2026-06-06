package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con datos de experiencia laboral.")
public record CandidateExperienceResponse(
        Long id,
        String companyName,
        String client,
        String project,
        String jobTitle,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        Boolean currentJob,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
