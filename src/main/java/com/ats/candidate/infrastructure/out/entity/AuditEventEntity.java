package com.ats.candidate.infrastructure.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_event")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_id")
    Long candidateId;

    String action;

    @Column(name = "entity_name")
    String entityName;

    @Column(name = "entity_id")
    Long entityId;

    @Lob
    String detail;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "created_by")
    Long createdBy;
}
