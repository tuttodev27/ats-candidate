package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado posible del postulante.")
public record CandidateStatusResponse(
        @Schema(description = "Codigo del estado.", example = "NEW")
        String code,
        @Schema(description = "Etiqueta descriptiva del estado.", example = "Nuevo")
        String label
) {
}
