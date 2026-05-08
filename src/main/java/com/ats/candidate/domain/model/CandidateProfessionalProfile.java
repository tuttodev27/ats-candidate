package com.ats.candidate.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateProfessionalProfile {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateId;
    String headline;
    String summary;
    String latestPosition;
    Long experienceRangeId;
    Integer yearsExperience;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
