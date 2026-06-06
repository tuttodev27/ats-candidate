package com.ats.candidate.infrastructure.out.storage;

import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.model.StoredAttachment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LocalAttachmentStorageAdapterTest {

    @TempDir
    private Path tempDir;

    @Test
    void storeWritesFileInsideCandidateDirectoryAndReturnsRelativeUrl() throws Exception {
        LocalAttachmentStorageAdapter adapter = new LocalAttachmentStorageAdapter(tempDir.toString());
        byte[] content = "pdf-content".getBytes(StandardCharsets.UTF_8);
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("my cv.pdf")
                .contentType("application/pdf")
                .size((long) content.length)
                .content(content)
                .uploadedBy(7L)
                .build();

        StoredAttachment stored = adapter.store(10L, upload);

        assertThat(stored.getFileName()).isEqualTo("my cv.pdf");
        assertThat(stored.getFileUrl()).startsWith("candidates/10/");
        assertThat(stored.getFileUrl()).endsWith("-my cv.pdf");

        Path storedPath = tempDir.resolve(stored.getFileUrl()).normalize();
        assertThat(storedPath).startsWith(tempDir.resolve("candidates").resolve("10"));
        assertThat(Files.readAllBytes(storedPath)).isEqualTo(content);
     }

    @Test
    void loadReadsStoredFileSuccessfully() throws Exception {
        LocalAttachmentStorageAdapter adapter = new LocalAttachmentStorageAdapter(tempDir.toString());
        byte[] content = "some-file-bytes".getBytes(StandardCharsets.UTF_8);
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv_to_load.pdf")
                .contentType("application/pdf")
                .size((long) content.length)
                .content(content)
                .uploadedBy(7L)
                .build();

        StoredAttachment stored = adapter.store(10L, upload);
        byte[] loadedContent = adapter.load(stored.getFileUrl());
        
        assertThat(loadedContent).isEqualTo(content);
    }
}
