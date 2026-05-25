package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Datos de experiencia laboral del postulante.")
public record CandidateExperienceRequest(
        @NotBlank
        @Size(max = 120)
        @Schema(description = "Nombre de la empresa.", example = "Google")
        String companyName,

        @Size(max = 120)
        @Schema(description = "Nombre del cliente.", example = "Alphabet")
        String client,

        @Size(max = 120)
        @Schema(description = "Nombre del proyecto.", example = "Search Engine")
        String project,

        @NotBlank
        @Size(max = 120)
        @Schema(description = "Cargo / Titulo del puesto.", example = "Software Engineer")
        String jobTitle,

        @Size(max = 180)
        @Schema(description = "Ubicacion de la empresa.", example = "Mountain View, CA")
        String location,

        @Schema(description = "Fecha de inicio.", example = "2020-01-01")
        LocalDate startDate,

        @Schema(description = "Fecha de finalizacion.", example = "2022-12-31")
        LocalDate endDate,

        @Schema(description = "Indica si es el trabajo actual.", example = "false")
        Boolean currentJob
) {}
