package com.ats.candidate.infrastructure.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_parse_result")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateParseResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_id", nullable = false)
    Long candidateId;

    @Column(name = "attachment_id")
    Long attachmentId;

    @Column(name = "raw_text", columnDefinition = "text")
    String rawText;

    @Column(name = "parsed_json", columnDefinition = "text")
    String parsedJson;

    @Column(name = "parser_version")
    String parserVersion;

    String status;

    @Column(name = "error_message", columnDefinition = "text")
    String errorMessage;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
