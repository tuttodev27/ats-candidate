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
@Table(name = "candidate_availability")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateAvailabilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_id", nullable = false)
    Long candidateId;

    String availability;

    @Column(name = "available_from")
    LocalDateTime availableFrom;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
