package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.port.out.repository.AttachmentRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.AttachmentPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.AttachmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AttachmentRepositoryAdapter implements AttachmentRepositoryPort {

    private final AttachmentJpaRepository attachmentJpaRepository;
    private final AttachmentPersistenceMapper attachmentPersistenceMapper;

    public AttachmentRepositoryAdapter(
            AttachmentJpaRepository attachmentJpaRepository,
            AttachmentPersistenceMapper attachmentPersistenceMapper
    ) {
        this.attachmentJpaRepository = attachmentJpaRepository;
        this.attachmentPersistenceMapper = attachmentPersistenceMapper;
    }

    @Override
    public Attachment save(Attachment attachment) {
        return attachmentPersistenceMapper.toDomain(
                attachmentJpaRepository.save(attachmentPersistenceMapper.toEntity(attachment))
        );
    }

    @Override
    public List<Attachment> findByCandidateId(Long candidateId) {
        return attachmentJpaRepository.findByCandidateIdOrderByUploadedAtDesc(candidateId)
                .stream()
                .map(attachmentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Attachment> findById(Long id) {
        return attachmentJpaRepository.findById(id)
                .map(attachmentPersistenceMapper::toDomain);
    }
}
