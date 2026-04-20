package com.ats.candidate.domain.model;

import lombok.*;
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
public class CandidateExperience {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateId;
    String companyName;
    String client;
    String project;
    String jobTitle;
    String location;
    LocalDate startDate;
    LocalDate endDate;
    Boolean currentJob;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    Set<CandidateExperienceDescription> descriptions;
    Set<CandidateExperienceTechnology> technologies;
}
