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
import java.util.List;
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
    String countryCode;
    String code;
    LocalDate birthDate;
    String location;
    String linkedinUrl;
    String githubUrl;
    String latestPosition;
    Integer yearsExperience;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Boolean active;
    Long createdBy;
    Long updatedBy;

    Set<CandidateExperience> experiences;
    Set<CandidateNote> notes;
    Set<CandidateLanguage> languages;
    Set<CandidateSoftSkill> softSkills;
    Set<CandidateHardSkill> hardSkills;
    Set<Attachment> attachments;
    Set<CandidateState> states;
    Set<CandidateAvailability> availabilities;
    Set<AuditEvent> auditEvents;
}
