package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Archivo adjunto del postulante.")
public record AttachmentResponse(
        Long id,
        Long candidateId,
        String fileName,
        String fileUrl,
        String fileType,
        Long fileSize,
        String checksum,
        LocalDateTime uploadedAt,
        Long uploadedBy,
        String parseStatus
) {
}
