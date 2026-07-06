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

@Entity
@Table(name = "candidate_experience_description")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateExperienceDescriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_experience_id", nullable = false)
    Long candidateExperienceId;

    @Column(name = "order_number")
    Integer orderNumber;

    @Column(columnDefinition = "text")
    String description;
}
