package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Attachment;

import java.util.List;

public interface AttachmentRepositoryPort {
    Attachment save(Attachment attachment);
    List<Attachment> findByCandidateId(Long candidateId);
}
