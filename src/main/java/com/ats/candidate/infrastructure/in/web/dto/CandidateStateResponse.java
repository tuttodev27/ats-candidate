package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Cambio de estado del postulante.")
public record CandidateStateResponse(
        @Schema(description = "Identificador del cambio de estado.", example = "1")
        Long id,

        @Schema(description = "Estado anterior del postulante.", example = "IN_REVIEW")
        String previousState,

        @Schema(description = "Nuevo estado del postulante.", example = "INTERVIEW")
        String newState,

        @Schema(description = "Identificador del reclutador que realizo el cambio.", example = "1")
        Long changedBy,

        @Schema(description = "Fecha y hora del cambio.", example = "2026-04-20T12:23:57.829836973")
        LocalDateTime changedAt
) {
}
