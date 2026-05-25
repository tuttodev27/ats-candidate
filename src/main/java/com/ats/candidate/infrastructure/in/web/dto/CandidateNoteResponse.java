package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con observaciones o notas.")
public record CandidateNoteResponse(
        Long id,
        String note,
        String createdAt,
        String createdBy
) {}
