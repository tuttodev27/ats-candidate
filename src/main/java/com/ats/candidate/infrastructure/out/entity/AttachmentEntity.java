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
@Table(name = "attachment")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "candidate_id", nullable = false)
    Long candidateId;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_url")
    String fileUrl;

    @Column(name = "file_type")
    String fileType;

    @Column(name = "file_size")
    Long fileSize;

    String checksum;

    @Column(name = "uploaded_at")
    LocalDateTime uploadedAt;

    @Column(name = "uploaded_by")
    Long uploadedBy;

    @Column(name = "parsed_at")
    LocalDateTime parsedAt;

    @Column(name = "parse_status")
    String parseStatus;

    @Lob
    @Column(name = "parse_error")
    String parseError;
}
