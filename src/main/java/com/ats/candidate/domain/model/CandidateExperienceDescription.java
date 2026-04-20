package com.ats.candidate.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateExperienceDescription {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateExperienceId;
    Integer orderNumber;
    String description;
}
