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
public class CandidateParseResult {
    @EqualsAndHashCode.Include
    Long id;
    Long candidateId;
    Long attachmentId;
    String rawText;
    String parsedJson;
    String parserVersion;
    String status;
    String errorMessage;
    LocalDateTime createdAt;
}
