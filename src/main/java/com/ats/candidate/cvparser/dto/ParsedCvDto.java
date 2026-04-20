package com.ats.candidate.cvparser.dto;

import java.util.List;

public record ParsedCvDto(
	ParsedPersonalDataDto personalData,
	ParsedProfessionalProfileDto professionalProfile,
	List<ParsedStudyDto> studies,
	List<ParsedCertificationDto> certifications,
	List<ParsedExperienceDto> experiences,
	List<String> hardSkills,
	List<String> softSkills,
	List<ParsedLanguageDto> languages,
	ParsedAttachmentDto attachment
) {
}
