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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_language")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateLanguageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_id", nullable = false)
    Long candidateId;

    @Column(name = "language_id", nullable = false)
    Long languageId;

    @Column(name = "language_level_id")
    Long languageLevelId;

    String source;
    BigDecimal confidence;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
