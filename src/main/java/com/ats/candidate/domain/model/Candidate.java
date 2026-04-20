package com.ats.candidate.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Candidate {

    @EqualsAndHashCode.Include
    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String identityDocument;
    String countryCode;
    String code;
    LocalDate birthDate;
    String location;
    String linkedinUrl;
    String githubUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Boolean active;
    Long createdBy;
    Long updatedBy;

    CandidateProfessionalProfile professionalProfile;
    Set<CandidateExperience> experiences;
    Set<CandidateEducation> educations;
    Set<CandidateCertification> certifications;
    Set<CandidateNote> notes;
    Set<CandidateLanguage> languages;
    Set<CandidateSoftSkill> softSkills;
    Set<CandidateHardSkill> hardSkills;
    Set<Attachment> attachments;
    Set<CandidateState> states;
    Set<CandidateAvailability> availabilities;
    Set<CandidateParseResult> parseResults;
    Set<AuditEvent> auditEvents;
}
