package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentJpaRepository extends JpaRepository<AttachmentEntity, Long> {
    List<AttachmentEntity> findByCandidateIdOrderByUploadedAtDesc(Long candidateId);
}
