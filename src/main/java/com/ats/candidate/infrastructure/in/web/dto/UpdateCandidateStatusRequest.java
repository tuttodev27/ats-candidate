package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data required to update candidate status.")
public record UpdateCandidateStatusRequest(
        @NotBlank
        @Schema(description = "New candidate status.", example = "IN_REVIEW")
        String status
) {
}
