package com.ats.candidate.cvparser.dto;

public record ParsedAttachmentDto(
	String fileName,
	String contentType
) {
}
