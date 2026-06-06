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
public class CandidateState {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateId;
    String state;
    java.time.LocalDateTime createdAt;
    Long createdBy;
}
