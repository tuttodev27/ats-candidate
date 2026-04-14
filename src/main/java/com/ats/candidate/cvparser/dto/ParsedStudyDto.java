package com.ats.candidate.cvparser.dto;

import java.time.LocalDate;

public record ParsedStudyDto(
	String educationLevel,
	String degree,
	String institution,
	LocalDate startDate,
	LocalDate endDate
) {
}
