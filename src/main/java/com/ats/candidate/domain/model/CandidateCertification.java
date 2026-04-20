package com.ats.candidate.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateCertification {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateId;
    String name;
    String issuer;
    Integer year;
    LocalDate expirationDate;
    String credentialUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
