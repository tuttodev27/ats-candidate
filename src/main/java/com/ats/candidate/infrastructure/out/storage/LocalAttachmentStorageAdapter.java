package com.ats.candidate.infrastructure.out.storage;

import com.ats.candidate.domain.exception.AttachmentStorageException;
import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.model.StoredAttachment;
import com.ats.candidate.domain.port.out.storage.AttachmentStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalAttachmentStorageAdapter implements AttachmentStoragePort {

    private final Path storageRoot;

    public LocalAttachmentStorageAdapter(
            @Value("${app.attachments.storage-path:build/uploads}") String storagePath
    ) {
        this.storageRoot = Path.of(storagePath).toAbsolutePath().normalize();
    }

    @Override
    public StoredAttachment store(Long candidateId, AttachmentUpload upload) {
        String cleanFileName = StringUtils.cleanPath(upload.getOriginalFileName());
        String storedFileName = UUID.randomUUID() + "-" + cleanFileName;
        Path candidateDirectory = storageRoot.resolve("candidates").resolve(String.valueOf(candidateId));
        Path target = candidateDirectory.resolve(storedFileName).normalize();

        try {
            Files.createDirectories(candidateDirectory);
            Files.write(target, upload.getContent());
            return StoredAttachment.builder()
                    .fileName(cleanFileName)
                    .fileUrl(storageRoot.relativize(target).toString())
                    .build();
        } catch (IOException ex) {
            throw new AttachmentStorageException("Could not store attachment file", ex);
        }
    }
}
