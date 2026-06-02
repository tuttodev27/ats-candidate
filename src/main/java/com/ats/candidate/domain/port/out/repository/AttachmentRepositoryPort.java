package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Attachment;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepositoryPort {
    Attachment save(Attachment attachment);
    List<Attachment> findByCandidateId(Long candidateId);
    Optional<Attachment> findById(Long id);
}
