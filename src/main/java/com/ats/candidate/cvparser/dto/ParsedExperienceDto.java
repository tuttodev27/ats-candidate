package com.ats.candidate.cvparser.dto;

import java.time.LocalDate;
import java.util.List;

public record ParsedExperienceDto(
	String companyName,
	String client,
	String project,
	String jobTitle,
	String location,
	LocalDate startDate,
	LocalDate endDate,
	boolean currentJob,
	List<String> description,
	List<String> technologies
) {
}
