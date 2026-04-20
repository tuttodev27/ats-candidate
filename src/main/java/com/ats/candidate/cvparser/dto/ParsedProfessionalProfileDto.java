package com.ats.candidate.cvparser.dto;

public record ParsedProfessionalProfileDto(
	String headline,
	String latestPosition,
	Integer yearsExperience,
	String summary
) {
}
