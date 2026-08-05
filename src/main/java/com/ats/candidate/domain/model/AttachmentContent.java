package com.ats.candidate.domain.model;

public record AttachmentContent(
        byte[] content,
        String fileName,
        String fileType
) {
}
