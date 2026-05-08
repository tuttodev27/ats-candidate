package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.CandidateNotFoundException;
import com.ats.candidate.domain.exception.InvalidAttachmentException;
import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.model.StoredAttachment;
import com.ats.candidate.domain.port.in.usecase.AttachmentUseCase;
import com.ats.candidate.domain.port.out.repository.AttachmentRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.domain.port.out.storage.AttachmentStoragePort;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
public class AttachmentService implements AttachmentUseCase {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PARSE_STATUS_PENDING = "PENDING";

    private final CandidateRepositoryPort candidateRepositoryPort;
    private final AttachmentRepositoryPort attachmentRepositoryPort;
    private final AttachmentStoragePort attachmentStoragePort;

    public AttachmentService(
            CandidateRepositoryPort candidateRepositoryPort,
            AttachmentRepositoryPort attachmentRepositoryPort,
            AttachmentStoragePort attachmentStoragePort
    ) {
        this.candidateRepositoryPort = candidateRepositoryPort;
        this.attachmentRepositoryPort = attachmentRepositoryPort;
        this.attachmentStoragePort = attachmentStoragePort;
    }

    @Override
    public Attachment uploadCv(Long candidateId, AttachmentUpload upload) {
        if (!candidateRepositoryPort.existsById(candidateId)) {
            throw new CandidateNotFoundException(candidateId);
        }
        validateUpload(upload);

        StoredAttachment storedAttachment = attachmentStoragePort.store(candidateId, upload);
        Attachment attachment = Attachment.builder()
                .candidateId(candidateId)
                .fileName(storedAttachment.getFileName())
                .fileUrl(storedAttachment.getFileUrl())
                .fileType(PDF_CONTENT_TYPE)
                .fileSize(upload.getSize())
                .checksum(calculateSha256(upload.getContent()))
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(upload.getUploadedBy())
                .parseStatus(PARSE_STATUS_PENDING)
                .build();

        return attachmentRepositoryPort.save(attachment);
    }

    @Override
    public List<Attachment> listByCandidateId(Long candidateId) {
        if (!candidateRepositoryPort.existsById(candidateId)) {
            throw new CandidateNotFoundException(candidateId);
        }
        return attachmentRepositoryPort.findByCandidateId(candidateId);
    }

    private void validateUpload(AttachmentUpload upload) {
        if (upload == null || upload.getContent() == null || upload.getContent().length == 0) {
            throw new InvalidAttachmentException("Attachment file is required");
        }
        if (upload.getSize() == null || upload.getSize() <= 0) {
            throw new InvalidAttachmentException("Attachment file is empty");
        }
        String fileName = upload.getOriginalFileName();
        if (fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new InvalidAttachmentException("Only PDF files are allowed");
        }
        String contentType = upload.getContentType();
        if (contentType != null && !contentType.isBlank() && !PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new InvalidAttachmentException("Only application/pdf content type is allowed");
        }
    }

    private String calculateSha256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available", ex);
        }
    }
}
