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
@Table(name = "candidate_note")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_id", nullable = false)
    Long candidateId;

    @Column(columnDefinition = "text")
    String note;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "created_by")
    Long createdBy;
}
