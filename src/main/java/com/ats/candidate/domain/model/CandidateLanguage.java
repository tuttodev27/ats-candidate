package com.ats.candidate.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateLanguage {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateId;
    Long languageId;
    Long languageLevelId;
    String source;
    BigDecimal confidence;
    LocalDateTime createdAt;
}
