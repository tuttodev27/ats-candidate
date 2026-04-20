package com.ats.candidate.domain.model;

import java.time.LocalDateTime;

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
public class Attachment {
    @EqualsAndHashCode.Include
    Long id;

    Long candidateId;

    String fileName;
    String fileUrl;
    String fileType;
    Long fileSize;
    String checksum;
    LocalDateTime uploadedAt;
    Long uploadedBy;
    LocalDateTime parsedAt;
    String parseStatus;
    String parseError;

}
