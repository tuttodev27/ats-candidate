package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Observaciones o notas sobre el postulante.")
public record CandidateNoteRequest(
        @NotBlank
        @Size(max = 2000)
        @Schema(description = "Contenido de la nota u observacion.", example = "Excelente perfil tecnico, demuestra buen dominio de arquitectura hexagonal.")
        String note
) {}
