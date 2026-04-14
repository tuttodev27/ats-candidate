package com.ats.candidate.cvparser.dto;

public record ParsedPersonalDataDto(
	String firstName,
	String lastName,
	String email,
	String phone,
	String identityDocument,
	String code,
	String location,
	String linkedin,
	String github
) {
}
