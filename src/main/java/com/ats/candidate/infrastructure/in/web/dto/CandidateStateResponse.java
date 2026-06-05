package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Estado del postulante en el proceso de seleccion.")
public record CandidateStateResponse(
        @Schema(description = "Identificador del estado.", example = "1")
        Long id,

        @Schema(description = "Estado del postulante.", example = "NEW")
        String state,

        @Schema(description = "Fecha y hora de creacion.", example = "2026-04-20T12:23:57.829836973")
        LocalDateTime createdAt,

        @Schema(description = "Identificador del reclutador que creo el estado.", example = "1")
        Long createdBy
) {
}
